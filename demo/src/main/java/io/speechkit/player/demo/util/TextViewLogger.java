package io.speechkit.player.demo.util;

import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;

public final class TextViewLogger implements Consumer<Logger.Item> {
    public static final class Builder {
        private final Context context;

        private int verboseTextColor = Color.LTGRAY;
        private int debugTextColor = 0xff5586C7;
        private int warnTextColor = Color.YELLOW;
        private int errorTextColor = Color.RED;
        private int infoTextColor = Color.GREEN;

        private float textSize;

        private TextView textView;

        public Builder(@NonNull final Context context) {
            this.context = context;
            setTextSizeSp(8);
        }

        public Builder setTextSizeSp(@Dimension(unit = Dimension.SP) final float textSize) {
            this.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    textSize, context.getResources().getDisplayMetrics());
            return this;
        }

        public Builder setTextSizePx(final float textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setVerboseTextColor(@ColorInt final int verboseTextColor) {
            this.verboseTextColor = verboseTextColor;
            return this;
        }

        public Builder setDebugTextColor(@ColorInt final int debugTextColor) {
            this.debugTextColor = debugTextColor;
            return this;
        }

        public Builder setWarnTextColor(@ColorInt final int warnTextColor) {
            this.warnTextColor = warnTextColor;
            return this;
        }

        public Builder setErrorTextColor(@ColorInt final int errorTextColor) {
            this.errorTextColor = errorTextColor;
            return this;
        }

        public Builder setInfoTextColor(@ColorInt final int infoTextColor) {
            this.infoTextColor = infoTextColor;
            return this;
        }

        public Builder setTextView(final TextView textView) {
            this.textView = textView;
            return this;
        }

        @NonNull
        public TextViewLogger build() {
            return new TextViewLogger(this);
        }
    }

    private final Handler mainHandler = new Handler(Looper.getMainLooper(), this::handle);

    private final TextView textView;

    private final int verboseTextColor;
    private final int debugTextColor;
    private final int warnTextColor;
    private final int errorTextColor;
    private final int infoTextColor;

    private TextViewLogger(final Builder builder) {
        if (builder.textView != null) {
            textView = builder.textView;
        } else {
            textView = new TextView(builder.context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.textSize);
            textView.setSingleLine(false);
            textView.setVerticalScrollBarEnabled(true);
        }
        textView.setMovementMethod(new ScrollingMovementMethod());

        verboseTextColor = builder.verboseTextColor;
        debugTextColor = builder.debugTextColor;
        warnTextColor = builder.warnTextColor;
        errorTextColor = builder.errorTextColor;
        infoTextColor = builder.infoTextColor;
    }

    public TextView getTextView() {
        return textView;
    }

    @Override
    public void accept(@NonNull final Logger.Item item) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            print(item);
        } else {
            mainHandler.obtainMessage(0, item).sendToTarget();
        }
    }

    private void print(final Logger.Item item) {
        final int start = textView.getText().length();
        textView.append(item.msg);
        final int end = textView.getText().length();

        final Spannable spannable = (Spannable) getTextView().getText();
        spannable.setSpan(new ForegroundColorSpan(selectColor(item.kind)), start, end, 0);
    }

    private boolean handle(final Message msg) {
        if (msg.what == 0) {
            print((Logger.Item) msg.obj);
        }
        return true;
    }

    private int selectColor(@Logger.Kind final int kind) {
        switch (kind) {
            case Logger.Kind.VERBOSE:
                return verboseTextColor;
            case Logger.Kind.DEBUG:
                return debugTextColor;
            case Logger.Kind.WARNING:
                return warnTextColor;
            case Logger.Kind.ERROR:
                return errorTextColor;
            default:
                return infoTextColor;
        }
    }
}
