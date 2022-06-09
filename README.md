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

# Usage

## Initialising the player

To instantiate a `Player` you have to use the `PlayerBuilder` contained in the SDK:

A `PlayerBuilder` instance is created from one of the following functions

### Via Podcast ID
`val builder = PlayerBuilder.forPodcastId(context, 1234, 5678)`

#### Parameters
__`context: Context`__ 

Context used to create the `Player`

__`projectId: Long`__

Id of the BeyondWords project ID

__`podcastId: Long`__

BeyondWords podcast ID

### Via External ID
`val builder = PlayerBuilder.forExternalId(context, 1234, "external id")`

#### Parameters
__`context: Context`__ 

Context used to create the `Player`

__`projectId: Long`__

Id of the BeyondWords project ID

__`externalId: Long`__

The internal article ID

### Via Article URL
`val builder = PlayerBuilder.forArticleUrl(context, 1234, "article url")`

#### Parameters
__`context: Context`__

Context used to create the `Player`

__`projectId: Long`__

Id of the BeyondWords project ID

__`articleUrl: String`__

The article url

## Configuring the player builder

```kotlin
import io.beyondwords.player.Player
import io.beyondwords.player.PlayerBuilder

...
val player = PlayerBuilder.forExternalId(context, projectid, externalid)
    .setPendingIntent(createPendingLaunchIntent())
    .enableUi(true)
    .setNotificationChannelId("notificationChannelId")
    .setNotificationId(1234)
    .setNotificationChannelNameId(R.string.my_custom_channel_name)
    .setNotificationChannelDescriptionId(R.string.my_custom_channel_description)
    .setNotificationSmallIconId(R.drawable.my_custom_small_notification_icon)
    .setNotificationLargeIconId(R.drawable.my_custom_large_notification_icon)
    .build()
...
```

### Parameters
__`setPendingIntent(intent: PendingIntent?)`__

`PendingIntent` to launch when clicking on the notification.

__`enableUi(enabledUi: Boolean)`__

Flag to toggle whether a notification should be shown or not.

__`setNotificationChannelId(notificationChannelId: String)`__

Set the notification channel id.

__`setNotificationId(notificationId: Int)`__

Set the notification id.

__`setNotificationChannelNameId(@StringRes notificationChannelNameId: Int)`__

App string resource for the channel name id .

__`setNotificationChannelDescriptionId(@StringRes notificationChannelDescriptionId: Int)`__

App string resource for the channel description id.

__`setNotificationSmallIconId(@DrawableRes notificationSmallIconId: Int)`__

App drawable resource for the small notification icon.

__`setNotificationLargeIconId(@DrawableRes notificationLargeIconId: Int)`__

App drawable resource for the large notification icon.

## Subscribing to events
```kotlin
...
val player = PlayerBuilder.forExternalId(...).build()

player.addListener(object::Player.EventListener() {
    override void onPrepare() {}

    override void onPlay(duration: Float, progress: Float, advertiser: String?, adUrl: String?) { }

    override void onPause(durationSec: Float, progressSec: Float) {}

    override void onTimeUpdate(durationSec: Float, progressSec: Float) {}

    override void onPlaybackRate(rate: Float) {}

    override void onEnded() {}
})
```

## Player Functions

__`fun release()`__

Releases the player. This must be called when the player is no longer required. The player must not be used after calling this method. 

__`fun addListener(listener: EventListener)`__

Register a listener to receive events from the player. The listener's methods will be called on the thread that was used to construct the player. However, if the thread used to construct the player does not have a Looper, then the listener will be called on the main thread.

__`fun removeListener(EventListener listener)`__

Unregister a listener. The listener will no longer receive events from the player.

__`fun isPrepared(): Boolean`__

Whether the player is prepared.

__`fun isReady(): Boolean`__

Whether the player is ready for playback.

__`fun isPlaying(): Boolean`__

Whether the player is playing.

__`fun isPaused(): Boolean`__

Determine if the player is currently paused.

__`fun isEnded(): Boolean`__

Determine if the player has ended.

__`fun play()`__

Begin playback of the audio.

__`fun pause()`__

Pause audio playback

__`fun fastForward()`__

Fast forward the audio playback by 10 seconds

__`fun rewind()`__

Rewind the audio playback by 10 seconds

__`fun seekTo(positionMs: Long)`__

Moves to a new location in the media

__Parameters__

`positionMs` Position to move to, in milliseconds

__`fun getCurrentTime(): Float`__

Get the current time, in seconds

__`fun getBufferedTime(): Float`__

Returns an estimate of the position in the current content up to which data is buffered, in seconds.

__`fun getDuration(): Float`__

Get the total duration of the audio article, in seconds

__`fun getRemainingTime(): Float`__

Get the time remaining on the audio article, in seconds

__`fun isPlayingAd(): Boolean`__

Check whether we're playing an unskippable ad

__`fun getPlaybackRate(): Float`__

Get the playback rate

__`fun setPlaybackRate(rate: Float): Float`__

Set the playback rate

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

## `PlaybackControlsView` functionality

__`fun getPlayer(): Player`__

Get the `Player` used in the view

__`fun setPlayer(player: Player)`__

Set the `Player` used in the view

__`fun setPlayPauseColour(@ColorInt colour: Int)`__

Set the colour of the play/pause buttons

__`fun setTitleTextColour(@ColorInt colour: Int)`__

Set the colour of the title text

__`fun setTitleLinkTextColour(@ColorInt colour: Int)`__

Set the colour of the title link text

__`fun setProgressTextColour(@ColorInt colour: Int)`__

Set the colour of the progress text

__`fun setSpeedTextColour(@ColorInt colour: Int)`__

Set the colour of the playback speed text

__`fun setBackgroundColour(@ColorInt colour: Int)`__

Set the colour of the player background

__`fun setBackgroundCornerRadius(radius: Float)`__

Set the background corner radius

__`fun setFastForwardRewindIconColour(@ColorInt colour: Int)`__

Set the colour of the fast-forward/rewind icons

__`fun setFastForwardRewindDisplayed(isDisplayed: Boolean)`__

Flag to display or hide the fast-forward/rewind buttons.

__`fun setRoundedProgressCorners(hasRoundedCorners: Boolean)`__

Set if the progress has rounded corners or not.

__`fun setProgressHeight(height: Int)`__

Set the height of the playback progress bar

__`fun setProgressBufferedColour(@ColorInt colour: Int)`__

Set the colour of the progress buffered section

__`fun setProgressPlayedColour(@ColorInt colour: Int)`__

Set the colour of the progress played section

__`fun setProgressUnplayedColour(@ColorInt colour: Int)`__

Set the colour of the progress unplayed section

__`fun setProgressBackgroundColour(@ColorInt colour: Int)`__

Set the colour of the progress background

__`fun updatePlayPauseIcons(@DrawableRes playIcon: Int, @DrawableRes pauseIcon: Int)`__

Update the icons of the play and pause buttons

__`fun setProgressUpdateListener(listener: ProgressUpdateListener?)`__

Set the listener to receive updates for playback progress

__`fun setTimeBarMinUpdateInterval(minUpdateIntervalMs: Int)`__

Set the time bar minimum update interval

__`fun show()`__

Show the player UI

__`fun hide()`__

Hide the player UI

__`fun resetPlayerUi()`__

Reset the player UI styling to the default values.

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
