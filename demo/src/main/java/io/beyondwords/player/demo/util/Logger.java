package io.beyondwords.player.demo.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.IntDef;
import androidx.core.util.Consumer;

public final class Logger {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {Kind.VERBOSE, Kind.DEBUG, Kind.WARNING, Kind.ERROR, Kind.INFO})
    public @interface Kind {
        int VERBOSE = 0;
        int DEBUG = 1;
        int WARNING = 2;
        int ERROR = 3;
        int INFO = 4;
    }

    public static final class Item {
        @Kind public final int kind;
        public final String msg;

        private Item(@Kind final int kind, final String msg) {
            this.kind = kind;
            this.msg = msg;
        }
    }

    private final CopyOnWriteArraySet<Consumer<Item>> consumers = new CopyOnWriteArraySet<>();

    private final SimpleDateFormat epochFormatter = new SimpleDateFormat("mm:ss.SSS");
    private final char[] kinds = new char[] {'V', 'D', 'W', 'E', 'I'};

    @AnyThread
    public void addConsumer(final Consumer<Item> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
    }

    @AnyThread
    public void removeConsumer(final Consumer<Item> consumer) {
        if (consumer != null) {
            consumers.remove(consumer);
        }
    }

    @AnyThread
    public void v(final String tag, final String msg) {
        Log.v(tag, msg);
        dispatch(create(Kind.VERBOSE, tag, msg));
    }

    @AnyThread
    public void v(final String tag, final String msg, final Throwable tr) {
        Log.v(tag, msg, tr);
        dispatch(create(Kind.VERBOSE, tag, msg, tr));
    }

    @AnyThread
    public void d(String tag, String msg) {
        Log.d(tag, msg);
        dispatch(create(Kind.DEBUG, tag, msg));
    }

    @AnyThread
    public void d(final String tag, final String msg, final Throwable tr) {
        Log.d(tag, msg, tr);
        dispatch(create(Kind.DEBUG, tag, msg, tr));
    }

    @AnyThread
    public void i(final String tag, final String msg) {
        Log.i(tag, msg);
        dispatch(create(Kind.INFO, tag, msg));
    }

    @AnyThread
    public void i(final String tag, final String msg, Throwable tr) {
        Log.i(tag, msg, tr);
        dispatch(create(Kind.INFO, tag, msg, tr));
    }

    @AnyThread
    public void w(final String tag, final String msg) {
        Log.w(tag, msg);
        dispatch(create(Kind.WARNING, tag, msg));
    }

    @AnyThread
    public void w(final String tag, final String msg, final Throwable tr) {
        Log.w(tag, msg, tr);
        dispatch(create(Kind.WARNING, tag, msg, tr));
    }

    @AnyThread
    public void w(final String tag, final Throwable tr) {
        Log.w(tag, tr);
        dispatch(create(Kind.WARNING, tag, "", tr));
    }

    @AnyThread
    public void e(final String tag, final String msg) {
        Log.e(tag, msg);
        dispatch(create(Kind.ERROR, tag, msg));
    }

    @AnyThread
    public void e(final String tag, final String msg, final Throwable tr) {
        Log.e(tag, msg, tr);
        dispatch(create(Kind.ERROR, tag, msg, tr));
    }

    private void dispatch(final Item item) {
        for (Consumer<Item> consumer : consumers) {
            consumer.accept(item);
        }
    }

    private Item create(@Kind final int kind, final String tag, final String msg) {
        return create(kind, tag, msg, null);
    }

    private Item create(@Kind final int kind, final String tag, final String msg, final Throwable tr) {
        final StringBuilder text = new StringBuilder()
            .append('\n').append(epochFormatter.format(new Date()))
            .append(' ').append(getThreadId())
            .append(' ').append(kinds[kind]).append('/').append(tag)
            .append(' ').append(msg);
        if (tr != null) {
            text.append('\n').append(Log.getStackTraceString(tr));
        }
        return new Item(kind, text.toString());
    }

    private static String getThreadId() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            return "main ";
        }
        final long id = Thread.currentThread().getId();
        if (id < 10) {
            return "0000" + id;
        }
        if (id < 100) {
            return "000" + id;
        }
        if (id < 1000) {
            return "00" + id;
        }
        if (id < 10000) {
            return "0" + id;
        }
        return Long.toString(id);
    }
}
