package io.beyondwords.player.demo

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "bw_demo_prefs"
private const val KEY_PROJECT_ID = "player_projectid"
private const val KEY_EXTERNAL_ID = "player_externalid"
private const val KEY_PODCAST_ID = "player_podcastid"
private const val KEY_ARTICLE_ID = "player_articleurl"
const val DEFAULT_PROJECT_ID = "9093"
const val DEFAULT_PODCAST_ID = "2653378"

internal class Prefs(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var projectId: String?
        get() = getString(KEY_PROJECT_ID)
        set(value) {
            setString(KEY_PROJECT_ID, value)
        }

    var externalId: String?
        get() = getString(KEY_EXTERNAL_ID)
        set(value) {
            setString(KEY_EXTERNAL_ID, value)
        }

    var podcastId: String?
        get() = getString(KEY_PODCAST_ID)
        set(value) {
            setString(KEY_PODCAST_ID, value)
        }

    var articleUrl: String?
        get() = getString(KEY_ARTICLE_ID)
        set(value) {
            setString(KEY_ARTICLE_ID, value)
        }

    private fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    private fun setString(key: String, value: String?) {
        sharedPreferences.edit {
            if (value.isNullOrBlank()) {
                putString(key, value)
            } else {
                remove(key)
            }
        }
    }
}