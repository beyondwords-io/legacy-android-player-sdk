# Quick Start #

### 1. Add repositories ###

```gradle
ext {
    // These 2 properties should be stored in your device gradle properties file and should contain your GitHub PAT user and token 
    getGithubUser = { project.properties["GITHUB_USER"] }
    getGithubToken = { project.properties["GITHUB_TOKEN"] }
}

repositories {
    ...
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/SpeechKit/beyondwords-android-sdk")
        credentials {
            username = getGithubUser()
            password = getGithubToken()
        }
    }
}
```

### 2. Add the BeyondWords SDK module dependency ###

Next add a dependency in the `build.gradle` file of your app module. The
following will add a dependency to the library:

```gradle
implementation 'io.beyondwords.android:player:2.X.X'
```

where `2.X.X` is your preferred version.
Currently available version is `2.0.0`.

### 3. Turn on Java 8 support ###

If not enabled already, you also need to turn on Java 8 support in all
`build.gradle` files depending on the BeyondWords SDK by adding the following to the
`android` section:

```gradle
compileOptions {
  targetCompatibility JavaVersion.VERSION_1_8
}
```

## Usage

To instantiate a `Player` you have to use the `PlayerBuilder` conatained in the SDK:

```java
import io.beyondwords.player.Player;
import io.beyondwords.player.PlayerBuilder;

...
final Player player = PlayerBuilder.forExternalId(context, projectid, externalid)
    .setPendingIntent(createPendingLaunchIntent())
    .enableUi(true)
    .build();
...
```

After creating a player, you can subscribe to its events:

```java
...
final Player player = PlayerBuilder.forExternalId(...).build();
...
player.addListener(new Player.EventListener() {
    public void onPrepare() {}

    public void onPlay(float duration, float progress, @Nullable String advertiser, @Nullable String adUrl) { }
    
    public void onPause(float durationSec, float progressSec) {}

    public void onTimeUpdate(float durationSec, float progressSec) {}

    public void onPlaybackRate(float rate) {}

    public void onEnded() {}
});
...
```

Player provides standard methods like: `play()/pause()`, `rewind()/fastForward()`, `seekTo()` and so on.

When you are done with the player you should call the `release()` method. The player must not be used after calling this method.

## Customising the default player UI

The SDK also conatins a UI-component: `PlaybackControlView`.

```java
import io.beyondwords.player.PlaybackControlView;
```

To use it simply instantiate it directly and add to the current view hierarchy, or just add it to your xml layout.

Then bind the player and playback control view:

```java
import io.beyondwords.player.Player;
import io.beyondwords.player.PlayerBuilder;
import io.beyondwords.player.PlaybackControlView;
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

## Fetching podcast metadata

This can be done in a similar way to play a podcast, you can specify either by using an external id, podcast id or an article url.

```java
import io.beyondwords.player.Player;

PodcastRetriever podcastRetriever = new PodcastRetriever();

// To fetch via an external id 
podcastRetriever.getViaExternalId(projectId, externalId, listener)

// To fetch via an podcast id 
podcastRetriever.getViaPodcastId(projectId, podcastId, listener)

// To fetch via an article url 
podcastRetriever.getViaArticleUrl(projectId, articleUrl, listener)

```

## Demo app

Run the following command in the terminal:
```
git clone https://github.com/SpeechKit/beyondwords-android-sdk-public.git ./beyondwords_sdk_demo
```

Then import the project to Android Studio:
```
File -> New -> Import Project… (navigate to beyondwords_sdk_demo dir)
```
