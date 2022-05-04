# Quick Start #

### 1. Add repositories ###

```gradle
repositories {
    ...
    maven {
        maven { url 'https://repo.repsy.io/mvn/speechkitio/speechkit_io' }
    }
}
```

#### Alternate repository
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
        url = uri("https://maven.pkg.github.com/SpeechKit/beyondwords-android-sdk-public")
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
Currently available version is `2.1.0`.

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

To instantiate a `Player` you have to use the `PlayerBuilder` contained in the SDK:

```kotlin
import io.beyondwords.player.Player
import io.beyondwords.player.PlayerBuilder

...
val player = PlayerBuilder.forExternalId(context, projectid, externalid)
    .setPendingIntent(createPendingLaunchIntent())
    .enableUi(true)
    .build()
...
```

After creating a player, you can subscribe to its events:

```java
...
val player = PlayerBuilder.forExternalId(...).build()
...
player.addListener(object::Player.EventListener() {
    override void onPrepare() {}

    override void onPlay(float duration, float progress, @Nullable String advertiser, @Nullable String adUrl) { }

    override void onPause(float durationSec, float progressSec) {}

    override void onTimeUpdate(float durationSec, float progressSec) {}

    override void onPlaybackRate(float rate) {}

    override void onEnded() {}
});
...
```

Player provides standard methods like: `play()/pause()`, `rewind()/fastForward()`, `seekTo()` and so on.

When you are done with the player you should call the `release()` method. The player must not be used after calling this method.

## Customising the default player UI

The SDK also contains a UI-component: `PlaybackControlView`.

```kotlin
import io.beyondwords.player.PlaybackControlView
```

To use it simply instantiate it directly and add to the current view hierarchy, or just add it to your xml layout.

Then bind the player and playback control view:

```kotlin
import io.beyondwords.player.Player
import io.beyondwords.player.PlayerBuilder
import io.beyondwords.player.PlaybackControlView
...
var player = PlayerBuilder.forExternalId(...).build()
...
val control = new PlaybackControlView(context)
control.setPlayer(player)
...
control.setPlayer(null)
...
player?.release()
player = null
```

## Fetching podcast metadata

This can be done in a similar way to play a podcast, you can specify either by using an external id, podcast id or an article url.

```kotlin
import io.beyondwords.player.Player;

val podcastRetriever = PodcastRetriever()

// To fetch via an external id
podcastRetriever.getViaExternalId(projectId, externalId, listener)

// To fetch via an podcast id
podcastRetriever.getViaPodcastId(projectId, podcastId, listener)

// To fetch via an article url
podcastRetriever.getViaArticleUrl(projectId, articleUrl, listener)
```

## Player Styling

The player can be styled from either code, xml or overriding xml resources

| Code         | XML           | Resource override    |
| ---------- | --------------- | ----------------------|
| playbackControlView.setRoundedProgressCorners | app:bw_rounded_progress_corners="false" | ```<bool name="bw_progress_rounded_corners"></bool>``` |
| playbackControlView.setBackgroundColour | app:bw_background_colour |  ```<color name="bw_background_colour"></color>``` |
| playbackControlView.setTitleTextColour | app:bw_title_text_colour | ```<color name="bw_title_text_colour"></color>``` |
| playbackControlView.setTitleLinkTextColour | app:bw_title_link_colour | ```<color name="bw_title_link_text_colour"></color>``` |
| playbackControlView.setSpeedTextColour | app:bw_speed_text_colour | ```<color name="bw_speed_text_colour"></color>``` |
| playbackControlView.setProgressTextColour | app:bw_progress_text_colour | ```<color name="bw_progress_text_colour"></color>``` |
| playbackControlView.setPlayPauseColour | app:bw_play_pause_colour | ```<color name="bw_play_button_colour"></color>``` |
| playbackControlView.setFastForwardRewindIconColour | app:bw_ff_rewind_icon_colour | ```<color name="bw_ff_rewind_icon_colour"></color>``` |
| playbackControlView.setFastForwardRewindDisplayed | app:bw_ff_rewind_icon_displayed | ```<bool name="bw_ff_rewind_icon_displayed"></bool>``` |
| playbackControlView.setBackgroundCornerRadius | app:bw_background_corner_radius | ```<dimen name="bw_background_corner_radius"></dimen>``` |
| playbackControlView.setProgressHeight | app:bw_progress_height |  ```<dimen name="bw_progress_height"></dimen>``` |
| playbackControlView.setProgressBackgroundColour | app:bw_progress_background_colour | ```<color name="bw_progress_background_colour"></color>``` |
| playbackControlView.setProgressPlayedColour | app:bw_progress_played_colour | ```<color name="bw_progress_played_colour"></color>``` |
| playbackControlView.setProgressBufferedColour | app:bw_progress_buffered_colour | ```<color name="bw_progress_buffered_colour"></color>``` |
| playbackControlView.updatePlayPauseIcons | app:bw_play_button_img and app:bw_pause_button_img  | R.drawable.ic_bw_play and R.drawable.ic_bw_pause |

## Added sticky player helper

A small helper class to anchor a player to the top or bottom of a ```ViewGroup```

``` kotlin
PlayerAnchorHelper.anchorPlayer(
	anchorToView = <view group to add helper inside>,
    playbackControlView = <player view to anchor>,
    anchor = <PlayerAnchor.TOP or PlayerAnchor.BOTTOM>
)
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
