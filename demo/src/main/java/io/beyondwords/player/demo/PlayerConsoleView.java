package io.beyondwords.player.demo;

import java.lang.reflect.Field;

import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.Gravity;
import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import io.speechkit.player.Player;

import io.speechkit.player.PlayerException;
import io.beyondwords.player.demo.util.Logger;
import io.beyondwords.player.demo.util.TextViewLogger;
import io.speechkit.player.demo.R;

public final class PlayerConsoleView implements Player.EventListener {
    private static final String TAG = "PlayerConsole";

    private static final String UNDEF = "undef";

    private final Logger logger;
    private final DrawerLayout drawer;
    private final TextViewLogger logView;

    private Player player;

    public PlayerConsoleView(@NonNull Activity activity, @ColorInt final int backgroundColor) {
        logger = new Logger();

        final View layout = activity.getLayoutInflater().inflate(R.layout.player_console_layout, null, false);
        final TextView textView = layout.findViewById(R.id.log_view);
        logView = new TextViewLogger.Builder(activity).setTextView(textView).build();
        logger.addConsumer(logView);

        layout.findViewById(R.id.clear_log_btn).setOnClickListener(v -> textView.setText(""));
        layout.findViewById(R.id.play_btn).setOnClickListener(v -> {
            if (player != null) {
                player.play();
                
            }
        });
        layout.findViewById(R.id.pause_btn).setOnClickListener(v -> {
            if (player != null) {
                player.pause();
            }
        });
        layout.findViewById(R.id.is_ready_btn).setOnClickListener(v -> {
            logger.d(TAG, "isReady()=" + (player != null && player.isReady()));
        });
        layout.findViewById(R.id.is_playing_btn).setOnClickListener(v -> {
            logger.d(TAG, "isPlaying()=" + (player != null && player.isPlaying()));
        });
        layout.findViewById(R.id.is_paused_btn).setOnClickListener(v -> {
            logger.d(TAG, "isPaused()=" + (player != null && player.isPaused()));
        });
        layout.findViewById(R.id.is_ended_btn).setOnClickListener(v -> {
            logger.d(TAG, "isEnded()=" + (player != null && player.isEnded()));
        });
        layout.findViewById(R.id.current_time_btn).setOnClickListener(v -> {
            logger.d(TAG, "getCurrentTime()=" + (player != null ? player.getCurrentTime() : UNDEF));
        });
        layout.findViewById(R.id.remaining_time_btn).setOnClickListener(v -> {
            logger.d(TAG, "getRemainingTime()=" + (player != null ? player.getRemainingTime() : UNDEF));
        });
        layout.findViewById(R.id.duration_btn).setOnClickListener(v -> {
            logger.d(TAG, "getDuration()=" + (player != null ? player.getDuration() : UNDEF));
        });

        drawer = new DrawerBuilder(activity)
            .setDrawerLayoutId(R.layout.drawer_layout)
            .setScrimColor(backgroundColor)
            .setViewToAdd(layout)
            .build();
    }

    public void release() {
        if (player != null) {
            player.removeListener(this);
            player = null;
        }
        logger.removeConsumer(logView);
    }

    public void setPlayer(final Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(this);
        }
        this.player = player;
        if (player != null) {
            player.addListener(this);
        }
    }

    public boolean isShown() {
        return drawer.isDrawerVisible(GravityCompat.START);
    }

    public void show() {
        drawer.openDrawer(GravityCompat.START);
    }

    public void hide() {
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onPlaybackRate(final float playbackRate) {
        logger.d(TAG, "onPlaybackRate, playbackRate=" + playbackRate);
    }

    @Override
    public void onPrepare() {
        logger.i(TAG, "onPrepare"
            + (player.getPlayerError() != null ? ", error=" + toString(player.getPlayerError()) : ""));
    }

    @Override
    public void onPlay(float duration, float progress, @Nullable String advertiser, @Nullable String adUrl) {
        logger.i(TAG, "onPlay, progress=" + progress + ", duration=" + duration + ", advertiser=" + advertiser + ", adUrl=" + adUrl);
    }

    @Override
    public void onPause(float duration, float progress) {
        logger.i(TAG, "onPause, progress=" + progress + ", duration=" + duration);
    }

    @Override
    public void onEnded() {
        logger.i(TAG, "onEnded");
    }

    private static String toString(final PlayerException error) {
        if (error == null) {
            return "";
        }
        return "PlayerException{"
            + "type=" + toString(error.type)
            + "|cause=" + error.getCause()
            + "|msg=" + error.getMessage()
            + "}"; 
    }

    private static String toString(@PlayerException.Type final int type) {
        switch (type) {
            case PlayerException.Type.SOURCE: return "net";
            default: return "unknown";
        }
    }

    private static final class DrawerBuilder {
        private final Activity activity;
        private int drawerLayoutId;
        private Integer scrimColor; 
        private View viewToAdd;

        DrawerBuilder(@NonNull final Activity activity) {
            this.activity = activity;
        }

        DrawerBuilder setDrawerLayoutId(@LayoutRes final int drawerLayoutId) {
            this.drawerLayoutId = drawerLayoutId;
            return this;
        }

        DrawerBuilder setScrimColor(@ColorInt final Integer scrimColor) {
            this.scrimColor = scrimColor;
            return this;
        }

        DrawerBuilder setViewToAdd(final View viewToAdd) {
            this.viewToAdd = viewToAdd;
            return this;
        }

        DrawerLayout build() {
            if (drawerLayoutId == 0) {
                throw new IllegalStateException();
            }

            final LayoutInflater inflater = activity.getLayoutInflater();
            final DrawerLayout result = (DrawerLayout) inflater.inflate(drawerLayoutId, null, false);

            if (scrimColor != null) {
                result.setScrimColor(scrimColor);
            }

            final ViewGroup rootView = activity.findViewById(android.R.id.content);
            final View contentView = rootView.getChildAt(0);

            rootView.removeView(contentView);

            result.addView(contentView,
                new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rootView.addView(result,
                new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            if (viewToAdd != null) {
                final DrawerLayout.LayoutParams layout = new DrawerLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.gravity = Gravity.START;
                result.addView(viewToAdd, layout);
            }

            try {
                final Field minMarginField = result.getClass().getDeclaredField("mMinDrawerMargin");
                minMarginField.setAccessible(true);
                minMarginField.set(result, 0);
            } catch (Exception ignored) {}

            return result;
        }
    }
}
