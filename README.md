# SpeechKit player #

`SpeechKit player` is an application level media player for Android that includes UI components to control the playbck of audio articles.
SpeechKit player is easy to integrate and use in application.

## Using SpeechKit player ##

SpeechKit player module can be obtained from [Bintray maven repository](https://bintray.com/).

### 1. Add repositories ###

You need to make sure you have the [Bintray maven repository](https://bintray.com/) repository in the `build.gradle` file in the root of your project:

```gradle
repositories {
    ...
    maven { url 'https://dl.bintray.com/spkt/maven' }
}
```

### 2. Add SpeechKit player module dependency ###

Next add a dependency in the `build.gradle` file of your app module. The
following will add a dependency to the library:

```gradle
implementation 'io.speechkit.android:player:1.X.X'
```

where `1.X.X` is your preferred version.
Currently available version is `1.0.0-alpha1`.

More information on the library that are available from maven repository
can be found on [Bintray][].

[Bintray]: https://bintray.com/beta/#/spkt/maven?tab=packages

### 3. Turn on Java 8 support ###

If not enabled already, you also need to turn on Java 8 support in all
`build.gradle` files depending on SpeechKit player, by adding the following to the
`android` section:

```gradle
compileOptions {
  targetCompatibility JavaVersion.VERSION_1_8
}
```

## Touching SpeechKit player

To instantiate a `Player`, you have to use the `PlayerBuilder` conatained in the sdk:

```java
import io.speechkit.player.Player;
import io.speechkit.player.PlayerBuilder;

...
final Player player = PlayerBuilder.forExternalId(context, projectid, externalid)
    .setPendingIntent(createPendingLaunchIntent())
    .enableUi(true)
    .build();
...
```

After creating a player, you can subscribe to its events.

```java
...
final Player player = PlayerBuilder.forExternalId(...).build();
...
player.addListener(new Player.EventListener() {
    public void onPrepare() {}

    public void onPlay(float durationSec, float progressSec) {}

    public void onPause(float durationSec, float progressSec) {}

    public void onTimeUpdate(float durationSec, float progressSec) {}

    public void onPlaybackRate(float rate) {}

    public void onEnded() {}
    
});
...
```

Player provides standard methods, like: `play()/pause()`, `rewind()/fastForward()`, `seekTo()` and so on.

When you will done with the player you should call the `release()` method.
The player must not be used after calling this method.

## Touching SpeechKit player UI

`SpeechKit player sdk` also conatins UI-component: `PlaybackControlView`.

```java
import io.speechkit.player.PlaybackControlView;
```

To use it, simply instantiate it directly and add to the current view hierarchy, or just add it to your xml layout.

Then bind the player and playback control view:

```java
import io.speechkit.player.Player;
import io.speechkit.player.PlayerBuilder;
import io.speechkit.player.PlaybackControlView;
...
final Player player = PlayerBuilder.forExternalId(...).build();
...
final PlaybackControlView control = new PlaybackControlView(context);
control.setPlayer(player);
...
control.setPlayer(null);
...
player.release();
player = null;
```
