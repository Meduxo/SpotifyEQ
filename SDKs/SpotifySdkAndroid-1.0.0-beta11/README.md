**IMPORTANT! The Spotify Android SDK is currently a beta release; its content
and functionality are likely to change significantly and without warning.**

Spotify Android SDK
===================

Welcome to Spotify Android SDK! This project is for people who wish to develop
Android applications containing Spotify-related functionality, such as audio streaming and
user authentication and authorization.

Note that by using this SDK, you accept our [Developer Terms of
Use](https://developer.spotify.com/developer-terms-of-use/).


Beta Release Information
========================

We're releasing this SDK early to gain feedback from the developer community
about the future of our Android SDK. Please file feedback about missing issues
or bugs over at our [issue tracker](https://github.com/spotify/android-sdk/issues),
making sure you search for existing issues and adding your voice to those rather
than duplicating.

For known issues and release notes, see the
[CHANGELOG.md](https://github.com/spotify/android-sdk/blob/master/CHANGELOG.md)
file.


Getting Started
===============

1. Download the latest [Spotify Android SDK](https://github.com/spotify/android-sdk/releases).
2. Have a look at the [beginner's tutorial](https://developer.spotify.com/technologies/spotify-android-sdk/tutorial/)
   on the Spotify Developer Website.


SDK's structure
===============

Spotify Android SDK consists of two libraries.
One of them handles authentication flow and the other one manages audio playback.
The libraries work well together but can also be used separately, for example if
the application doesn't need to play music it can use just Spotify Authentication module by itself.

Spotify Authentication
----------------------

This module is responsible for authenticating the user and fetching the access token
that can subsequently be used to play music or send requests to the Spotify Web API.

To add this library to your project copy the `spotify-auth-{version}.aar` file from the
[SDK archive](https://github.com/spotify/android-sdk/releases) to the `libs`
folder in your app project and add the reference to its `build.gradle` file.
For version `1.0.0-beta9` it would be:

```
compile 'com.spotify.sdk:spotify-auth:1.0.0-beta9@aar'
```

To learn more about working with authentication see the
[Authentication Guide](https://developer.spotify.com/technologies/spotify-android-sdk/android-sdk-authentication-guide/)
and the [API reference](https://developer.spotify.com/android-sdk-docs/authentication) on the developer site.

Spotify Player
--------------

The player module will play music from Spotify after the user logs in with the access token.
**Only Premium Spotify users will be able to log in and play music with this library.**.

To add this library to your project copy the `spotify-player-{version}.aar` file from the
[SDK archive](https://github.com/spotify/android-sdk/releases) to the `libs`
folder in your app project and add the reference to its `build.gradle` file.
For version `1.0.0-beta9` it would be:

```
compile 'com.spotify.sdk:spotify-player:1.0.0-beta9@aar'
```

To learn more about working with the player see the
[Beginner's Tutorial](https://developer.spotify.com/technologies/spotify-android-sdk/tutorial/)
and the [API reference](https://developer.spotify.com/android-sdk-docs/player) on the developer site.

Spotify Web API
---------------

The Web API wrapper is currently not a part of the SDK project but there are
a few open source [Web API libraries](https://developer.spotify.com/web-api/code-examples/#libraries)
available for Android.


Authenticating and Scopes
=========================

You can generate your application's Client ID, Client Secret and define your
callback URIs at the [My Applications](https://developer.spotify.com/my-applications/)
section of the Spotify Developer Website.

When connecting a user to your app, you *must* provide the scopes your
application needs to operate. A scope is a permission to access a certain part
of a user's account, and if you don't ask for the scopes you need you will
receive permission denied errors when trying to perform various tasks.

You do *not* need a scope to access non-user specific information, such as to
perform searches, look up metadata, etc. A full list of scopes can be found on
[Scopes](https://developer.spotify.com/web-api/using-scopes/) section of the
Spotify Developer Website.

If your application's scope needs change after a user is connected to your app, you
will need to throw out your stored credentials and re-authenticate the user with the
new scopes.

**Important:** Only ask for the scopes your application needs. Requesting playlist
access when your app doesn't use playlists, for example, is bad form.
