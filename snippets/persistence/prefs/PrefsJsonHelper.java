import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Handles serializing/deserializing and caching Serializable objects into SharedPreferences.
 *
 * @author Juraj Novak (inloop)
 */
public class PrefsJsonHelper {

    private final HashMap<String, Object> mJsonObjectCache;
    private final SharedPreferences mPreferences;
    private final Gson mGson;

    public PrefsJsonHelper(@NonNull SharedPreferences preferences, @NonNull Gson gson) {
        mJsonObjectCache = new HashMap<>();
        mPreferences = preferences;
        mGson = gson;
    }

    /**
     * Load and deserialize saved object from settings (or from hashmap cache).
     */
    public <T extends Serializable> T loadSettingObject(@NonNull String name, @NonNull Class<T> classType, @Nullable T defaultVal) {
        Object obj = mJsonObjectCache.get(name);
        if (obj != null) {
            return classType.cast(obj);
        } else {
            String json = mPreferences.getString(name, null);
            if (json != null) {
                return classType.cast(mGson.fromJson(json, classType));
            } else {
                return defaultVal;
            }
        }
    }

    /**
     * Load and deserialize saved object from settings (or from hashmap cache).
     */
    @Nullable
    public <T extends Serializable> T loadSettingObject(@NonNull String name, @NonNull Class<T> classType) {
        return loadSettingObject(name, classType, null);
    }

    /**
     * Save object (and serialize to json) in settings (and put to hashmap cache).
     */
    public <T extends Serializable> void saveSettingObject(@NonNull String name, @NonNull T object) {
        mJsonObjectCache.put(name, object);
        mPreferences.edit().putString(name, mGson.toJson(object)).apply();
    }

    /**
     * Remove object from settings (and cache).
     */
    public void removeSettingObject(@NonNull String name) {
        mJsonObjectCache.remove(name);
        mPreferences.edit().remove(name).apply();
    }
}
