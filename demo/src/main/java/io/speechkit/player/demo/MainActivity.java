package io.speechkit.player.demo;

import android.view.inputmethod.InputMethodManager;
import android.app.PendingIntent;
import android.content.Context;
import android.text.TextWatcher;
import android.widget.TextView;
import android.text.TextUtils;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import io.speechkit.player.PlaybackControlView;
import io.speechkit.player.PlayerBuilder;
import io.speechkit.player.Player;

public class MainActivity extends AppCompatActivity implements Player.EventListener, View.OnClickListener {
    private static final String TAG = "MainActivity";

    private PlayerConsoleView console;
    private PlaybackControlView controls;

    private TextInputLayout projectidEditor;
    private TextInputLayout externalidEditor;
    private TextInputLayout podcastidEditor;
    private TextInputLayout articleurlEditor;

    private TextWatcher textWatcher;

    private View loadExternalidBtn;
    private View loadExternalidSpinner;
    private View loadArticleurlBtn;
    private View loadArticleurlSpinner;
    private View loadPodcastidBtn;
    private View loadPodcastidSpinner;

    private CheckBox useUiBtn;

    private TextView libVersionView;

    private Player player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        console = new PlayerConsoleView(this, getResources().getColor(R.color.playerConsoleScrim));
        controls = findViewById(R.id.playback_control_view);

        projectidEditor = findViewById(R.id.player_projectid_text_input);
        externalidEditor = findViewById(R.id.player_externalid_text_input);
        podcastidEditor = findViewById(R.id.player_podcastid_text_input);
        articleurlEditor = findViewById(R.id.player_articleurl_text_input);

        loadExternalidBtn = findViewById(R.id.load_externalid_btn);
        loadExternalidSpinner = findViewById(R.id.load_externalid_spinner);
        loadPodcastidBtn = findViewById(R.id.load_podcastid_btn);
        loadPodcastidSpinner = findViewById(R.id.load_podcastid_spinner);
        loadArticleurlBtn = findViewById(R.id.load_articleurl_btn);
        loadArticleurlSpinner = findViewById(R.id.load_articleurl_spinner);

        useUiBtn = findViewById(R.id.player_use_ui_btn);

        libVersionView = findViewById(R.id.lib_version_view);

        loadExternalidBtn.setOnClickListener(this);
        loadPodcastidBtn.setOnClickListener(this);
        loadArticleurlBtn.setOnClickListener(this);

        textWatcher = new TextWatcherAdapter();
        projectidEditor.getEditText().addTextChangedListener(textWatcher);
        externalidEditor.getEditText().addTextChangedListener(textWatcher);
        podcastidEditor.getEditText().addTextChangedListener(textWatcher);
        articleurlEditor.getEditText().addTextChangedListener(textWatcher);

        projectidEditor.getEditText().setText(Prefs.getPlayerProjectid(this));
        externalidEditor.getEditText().setText(Prefs.getPlayerExternalid(this));
        podcastidEditor.getEditText().setText(Prefs.getPlayerPodcastid(this));
        articleurlEditor.getEditText().setText(Prefs.getPlayerArticleurl(this));

        libVersionView.setText("lib v" + BuildConfig.LIB_VERSION + "/release maven(prod)");

        loadPlayerIds();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        releasePlayer();
        console.release();
        super.onDestroy();
    }

    protected void onPause() {
        savePlayerIds();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (console.isShown()) {
            console.hide();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPlaybackRate(final float playbackRate) {}

    @Override
    public void onPrepare() {
        onAfterLoad();
        controls.setPlayer(player);
        controls.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlay(float duration, float progress) {}

    @Override
    public void onPause(float duration, float progress) {}

    @Override
    public void onEnded() {}

    @Override
    public void onTimeUpdate(final float duration, final float progress) {}

    @Override
    protected void onNewIntent(final Intent intent) {
        Log.v(TAG, "onNewIntent, intent=" + intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);

        onBeforeLoad();

        final PlayerBuilder builder;
        if (v == loadExternalidBtn) {
            builder = PlayerBuilder.forExternalId(this, getProjectid(), getExternalid());
            loadExternalidSpinner.setVisibility(View.VISIBLE);
        } else if (v == loadPodcastidBtn) {
            builder = PlayerBuilder.forPodcastId(this, getProjectid(), getPodcastid());
            loadPodcastidSpinner.setVisibility(View.VISIBLE);
        } else {
            builder = PlayerBuilder.forArticleUrl(this, getProjectid(), getArticleurl());
            loadArticleurlSpinner.setVisibility(View.VISIBLE);
        }
        createPlayer(builder);
        controls.setVisibility(View.GONE);
    }

    private Long getProjectid() {
        return toLong(projectidEditor.getEditText().getText());
    }

    private Long getPodcastid() {
        return toLong(podcastidEditor.getEditText().getText());
    }

    private static Long toLong(final CharSequence chars) {
        try {
            return Long.parseLong(chars.toString());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getExternalid() {
        try {
            final CharSequence text = externalidEditor.getEditText().getText();
            return !TextUtils.isEmpty(text) ? text.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getArticleurl() {
        try {
            final CharSequence text = articleurlEditor.getEditText().getText();
            return !TextUtils.isEmpty(text) ? text.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void onBeforeLoad() {
        projectidEditor.setEnabled(false);
        externalidEditor.setEnabled(false);
        podcastidEditor.setEnabled(false);
        articleurlEditor.setEnabled(false);
        loadExternalidBtn.setEnabled(false);
        loadPodcastidBtn.setEnabled(false);
        loadArticleurlBtn.setEnabled(false);
        useUiBtn.setEnabled(false);
    }

    private void onAfterLoad() {
        loadExternalidSpinner.setVisibility(View.GONE);
        loadPodcastidSpinner.setVisibility(View.GONE);
        loadArticleurlSpinner.setVisibility(View.GONE);
        projectidEditor.setEnabled(true);
        externalidEditor.setEnabled(true);
        podcastidEditor.setEnabled(true);
        articleurlEditor.setEnabled(true);
        useUiBtn.setEnabled(true);
        updateLoadBtnsEnablity();        
    }

    private void updateLoadBtnsEnablity() {
        final Long projectid = getProjectid();
        loadExternalidBtn.setEnabled(projectid != null && getExternalid() != null);
        loadPodcastidBtn.setEnabled(projectid != null && getPodcastid() != null);
        loadArticleurlBtn.setEnabled(projectid != null && getArticleurl() != null);
    }

    private PendingIntent createLaunchIntent() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        return intent != null ? PendingIntent.getActivity(this, 0, intent, 0) : null;
    }

    private void createPlayer(final PlayerBuilder builder) {
        releasePlayer();
        player = builder
            .setPendingIntent(createLaunchIntent())
            .enableUi(useUiBtn.isChecked())
            .build();
        player.addListener(this);
        console.setPlayer(player);
    }

    private void releasePlayer() {
        if (player != null) {
            console.setPlayer(null);
            controls.setPlayer(null);
            player.removeListener(this);
            player.release();
            player = null;
        }
    }

    private void loadPlayerIds() {
        final String projectId = Prefs.getPlayerProjectid(this);
        projectidEditor.getEditText().setText(!TextUtils.isEmpty(projectId) ? projectId : Prefs.DEFAULT_PROJECT_ID);
        final String externalid = Prefs.getPlayerExternalid(this);
        externalidEditor.getEditText().setText(!TextUtils.isEmpty(externalid) ? externalid : Prefs.DEFAULT_EXTERNAL_ID);
        podcastidEditor.getEditText().setText(Prefs.getPlayerPodcastid(this));
        articleurlEditor.getEditText().setText(Prefs.getPlayerArticleurl(this));
    }

    private void savePlayerIds() {
        Long number = getProjectid();
        Prefs.savePlayerProjectid(this, number != null ? String.valueOf(number) : null);
        number = getPodcastid();
        Prefs.savePlayerPodcastid(this, number != null ? String.valueOf(number) : null);
        Prefs.savePlayerArticleurl(this, getArticleurl());
        Prefs.savePlayerExternalid(this, getExternalid());
    }

    private class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            updateLoadBtnsEnablity();
        }
    }
}
