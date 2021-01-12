package io.speechkit.player.demo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class Prefs {
    static final String DEFAULT_PROJECT_ID = "6699";
    static final String DEFAULT_EXTERNAL_ID = "448374";

    private static final String PREFS_NAME = "spkt_demo_prefs";

    @Nullable
    public static String getPlayerProjectid(@NonNull final Context context) {
        return getString(context, "player_projectid", null);
    }

    public static void savePlayerProjectid(@NonNull final Context context, @Nullable final String value) {
        setString(context, "player_projectid", value);
    }

    @Nullable
    public static String getPlayerExternalid(@NonNull final Context context) {
        return getString(context, "player_externalid", null);
    }

    public static void savePlayerExternalid(@NonNull final Context context, @Nullable final String value) {
        setString(context, "player_externalid", value);
    }

    @Nullable
    public static String getPlayerPodcastid(@NonNull final Context context) {
        return getString(context, "player_podcastid", null);
    }

    public static void savePlayerPodcastid(@NonNull final Context context, @Nullable final String value) {
        setString(context, "player_podcastid", value);
    }

    @Nullable
    public static String getPlayerArticleurl(@NonNull final Context context) {
        return getString(context, "player_articleurl", null);
    }

    public static void savePlayerArticleurl(@NonNull final Context context, @Nullable final String value) {
        setString(context, "player_articleurl", value);
    }

    @Nullable
    private static String getString(final Context context, final String key, final String defValue) {
        return getPrefs(context).getString(key, defValue);
    }

    private static void setString(final Context context, final String key, final String value) {
        if (value != null) {
            getPrefs(context).edit().putString(key, value).commit();
        } else {
            getPrefs(context).edit().remove(key).commit();
        }
    }

    private static boolean getBoolean(final Context context, final String key, final boolean defValue) {
        return getPrefs(context).getBoolean(key, defValue);
    }

    private static void setBoolean(final Context context, final String key, final Boolean value) {
        if (value != null) {
            getPrefs(context).edit().putBoolean(key, value).commit();
        } else {
            getPrefs(context).edit().remove(key).commit();
        }
    }

    private static final SharedPreferences getPrefs(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private Prefs() {}
}
