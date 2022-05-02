package io.beyondwords.player.demo

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import io.beyondwords.player.PlaybackControlView
import io.beyondwords.player.Player
import io.beyondwords.player.PlayerBuilder

private const val TAG = "MainActivity"
private const val WAIT_ENDED_ON_RELEASE = true

class MainActivity : AppCompatActivity(), Player.EventListener {

    private var console: PlayerConsoleView? = null
    private var controls: PlaybackControlView? = null
    private var projectIdEditor: TextInputLayout? = null
    private var externalIdEditor: TextInputLayout? = null
    private var podcastIdEditor: TextInputLayout? = null
    private var articleUrlEditor: TextInputLayout? = null
    private var loadExternalIdBtn: View? = null
    private var loadArticleUrlBtn: View? = null
    private var loadPodcastIdBtn: View? = null
    private var loadingProgressBar: ProgressBar? = null
    private var loadingProgressText: TextView? = null
    private var displayNotificationBtn: CheckBox? = null
    private var useSkinnedPlayerBtn: CheckBox? = null
    private var displayFastForwardRewindBtn: CheckBox? = null
    private var libVersionView: TextView? = null
    private var rootLayout: ViewGroup? = null
    private var player: Player? = null
    private var prefs: Prefs? = null

    private val projectId: Long?
        get() {
            val idText = projectIdEditor?.editText?.text?.toString()
            return idText?.toLong()
        }

    private val podcastId: Long?
        get() {
            val idText = podcastIdEditor?.editText?.text?.toString()
            return if (idText.isNullOrBlank()) null else idText.toLong()
        }

    private val externalId: String?
        get() = externalIdEditor?.editText?.text?.toString()

    private val articleUrl: String?
        get() {
            val text: CharSequence = articleUrlEditor?.editText?.text ?: ""
            return if (!TextUtils.isEmpty(text)) text.toString() else null
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        prefs = Prefs(context = this)

        rootLayout = findViewById(R.id.root)
        console = PlayerConsoleView(this, ContextCompat.getColor(this, R.color.playerConsoleScrim))
        controls = findViewById(R.id.playback_control_view)

        loadExternalIdBtn = findViewById(R.id.load_externalid_btn)
        loadPodcastIdBtn = findViewById(R.id.load_podcastid_btn)
        loadArticleUrlBtn = findViewById(R.id.load_articleurl_btn)
        loadingProgressBar = findViewById(R.id.loading_progress_bar)
        loadingProgressText = findViewById(R.id.loading_progress_message)
        displayNotificationBtn = findViewById(R.id.player_display_notification_btn)
        libVersionView = findViewById(R.id.lib_version_view)
        useSkinnedPlayerBtn = findViewById(R.id.player_use_skinned_player_btn)
        displayFastForwardRewindBtn = findViewById(R.id.player_display_ff_rewind_buttons_btn)

        projectIdEditor = findViewById<TextInputLayout>(R.id.player_projectid_text_input).apply {
            val projectId = prefs?.projectId ?: DEFAULT_PROJECT_ID
            editText?.doOnTextChanged { _, _, _, _ -> updateLoadButtonState() }
            editText?.setText(projectId)
        }

        externalIdEditor = findViewById<TextInputLayout>(R.id.player_externalid_text_input).apply {
            val externalId = prefs?.externalId ?: ""
            editText?.doOnTextChanged { _, _, _, _ -> updateLoadButtonState() }
            editText?.setText(externalId)
        }

        podcastIdEditor = findViewById<TextInputLayout>(R.id.player_podcastid_text_input).apply {
            val podcastId = prefs?.podcastId ?: DEFAULT_PODCAST_ID
            editText?.doOnTextChanged { _, _, _, _ -> updateLoadButtonState() }
            editText?.setText(podcastId)
        }

        articleUrlEditor = findViewById<TextInputLayout>(R.id.player_articleurl_text_input).apply {
            val articleUrl = prefs?.articleUrl ?: ""
            editText?.doOnTextChanged { _, _, _, _ -> updateLoadButtonState() }
            editText?.setText(articleUrl)
        }

        loadExternalIdBtn?.setOnClickListener {
            loadFromExternalId()
        }

        loadPodcastIdBtn?.setOnClickListener {
            loadFromPodcastId()
        }

        loadArticleUrlBtn?.setOnClickListener {
            loadFromArticleUrl()
        }

        useSkinnedPlayerBtn?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                customisePlayerUi()
            } else {
                controls?.resetPlayerUi()
            }
        }

        displayFastForwardRewindBtn?.setOnCheckedChangeListener { _, isChecked ->
            controls?.setFastForwardRewindDisplayed(isChecked)
        }

        loadingProgressBar?.isVisible = false
        loadingProgressText?.isVisible = false

        initialiseLibraryVersionText()
    }

    private fun customisePlayerUi() {
        controls?.apply {
            setProgressHeight(resources.getDimensionPixelSize(R.dimen.keyline_1))
            setRoundedProgressCorners(true)
            setBackgroundColour(getColour(R.color.alternative_background_colour))
            setTitleTextColour(getColour(R.color.white))
            setTitleLinkTextColour(getColour(R.color.white))
            setSpeedTextColour(getColour(R.color.white))
            setProgressTextColour(getColour(R.color.white))
            setPlayPauseColour(getColour(R.color.white))
            setFastForwardRewindIconColour(getColour(R.color.white))
            setBackgroundCornerRadius(resources.getDimension(R.dimen.keyline_1))
            setProgressBackgroundColour(getColour(R.color.alternative_progress_background_colour))
            setProgressPlayedColour(getColour(R.color.white))
            setProgressUnplayedColour(getColour(R.color.white))
            setProgressBufferedColour(getColour(R.color.alternative_progress_buffered_colour))
            updatePlayPauseIcons(R.drawable.ic_alt_play_icon, R.drawable.ic_alt_pause_icon)
        }
    }

    @ColorInt
    private fun getColour(@ColorRes colour: Int) = ContextCompat.getColor(this, colour)

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        releasePlayer()
        console?.release()
        super.onDestroy()
    }

    override fun onPause() {
        savePlayerIds()
        super<AppCompatActivity>.onPause()
    }

    override fun onBackPressed() {
        if (console?.isShown == true) {
            console?.hide()
            return
        }
        super.onBackPressed()
    }

    override fun onPlaybackRate(playbackRate: Float) {}

    override fun onPrepare() {
        Log.v(TAG, "onPrepare started")
        onAfterLoad()
        controls?.player = player
        controls?.isVisible = true
        Log.v(TAG, "onPrepare finished")
    }

    override fun onPlay(duration: Float, progress: Float, advertiser: String?, adUrl: String?) {
        Log.v(TAG, "onPlay")
        Log.v(TAG, "Advertiser: $advertiser")
        Log.v(TAG, "Advert external URL: $adUrl")
    }

    override fun onReleased() {
        Log.v(TAG, "onReleased")
    }

    override fun onPause(duration: Float, progress: Float) {
        Log.v(TAG, "onPause")
    }

    override fun onEnded() {
        Log.v(TAG, "onEnded")
    }

    override fun onNewIntent(intent: Intent) {
        Log.v(TAG, "onNewIntent, intent=$intent")
        super.onNewIntent(intent)
    }

    private fun loadAudioResource(loadResource: () -> PlayerBuilder) {
        hideKeyboard()
        disableViews()

        loadingProgressBar?.isVisible = true
        loadingProgressText?.isVisible = true

        val builder = loadResource.invoke()
        createPlayer(builder)

        controls?.isVisible = false
    }

    private fun hideKeyboard() {
        val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(
            window.decorView.rootView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun loadFromExternalId() {
        loadAudioResource {
            loadingProgressText?.setText(R.string.player_loading_from_external_id)
            PlayerBuilder.forExternalId(this, projectId!!, externalId!!)
        }
    }

    private fun loadFromPodcastId() {
        loadAudioResource {
            loadingProgressText?.setText(R.string.player_loading_from_podcast_id)
            PlayerBuilder.forPodcastId(this, projectId!!, podcastId!!)
        }
    }

    private fun loadFromArticleUrl() {
        loadAudioResource {
            loadingProgressText?.setText(R.string.player_loading_from_article_url)
            PlayerBuilder.forArticleUrl(this, projectId!!, articleUrl!!)
        }
    }

    private fun disableViews() {
        projectIdEditor?.isEnabled = false
        externalIdEditor?.isEnabled = false
        podcastIdEditor?.isEnabled = false
        articleUrlEditor?.isEnabled = false
        loadExternalIdBtn?.isEnabled = false
        loadPodcastIdBtn?.isEnabled = false
        loadArticleUrlBtn?.isEnabled = false
        displayNotificationBtn?.isEnabled = false
    }

    private fun onAfterLoad() {
        loadingProgressBar?.isVisible = false
        loadingProgressText?.isVisible = false

        projectIdEditor?.isEnabled = true
        externalIdEditor?.isEnabled = true
        podcastIdEditor?.isEnabled = true
        articleUrlEditor?.isEnabled = true
        displayNotificationBtn?.isEnabled = true

        updateLoadButtonState()
    }

    private fun updateLoadButtonState() {
        val projectId = projectId

        loadExternalIdBtn?.isEnabled = projectId != null && !externalId.isNullOrBlank()
        loadPodcastIdBtn?.isEnabled = projectId != null && podcastId != null
        loadArticleUrlBtn?.isEnabled = projectId != null && !articleUrl.isNullOrBlank()
    }

    private fun createLaunchIntent(): PendingIntent? {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val flags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        return if (intent != null) PendingIntent.getActivity(this, 0, intent, flags) else null
    }

    private fun createPlayer(builder: PlayerBuilder) {
        val isChecked = displayNotificationBtn?.isChecked ?: false
        releasePlayer()

        player = builder
            .setPendingIntent(createLaunchIntent())
            .enableUi(isChecked)
            .build()
        player?.addListener(this)
        console?.setPlayer(player)
    }

    private fun releasePlayer() {
        if (player != null) {
            console?.setPlayer(null)
            controls?.player = null

            //We need to postpone removing listener to make onEnded calling if needed
            if (!WAIT_ENDED_ON_RELEASE) {
                player?.removeListener(this)
            }
            player?.release()

            if (WAIT_ENDED_ON_RELEASE) {
                player?.removeListener(this)
            }
            player = null
        }
    }

    private fun savePlayerIds() {
        prefs?.apply {
            this.projectId = projectId
            this.podcastId = podcastId
            this.articleUrl = articleUrl
            this.externalId = externalId
        }
    }

    private fun initialiseLibraryVersionText() {
        libVersionView?.text = String.format(
            getString(R.string.library_version_template),
            BuildConfig.LIB_VERSION,
            BuildConfig.BUILD_TYPE.lowercase()
        )
    }
}