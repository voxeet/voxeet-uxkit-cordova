---
title: Voxeet Cordova
description: Interact with the Voxeet ConferenceKit
---


# cordova-plugin-voxeet

This plugin is bridging the Voxeet's Toolkit/ConferenceKit calls. You can interacting with the plugin after the Cordova's deviceReady event.

It is mandatory that you added :
  - the iOS platform `cordova platform add ios`
  - the Android platform `cordova platform add android`

## Installation

    cordova plugin add cordova-plugin-voxeet@0.0.6

### Android

The build.gradle must be edited to add :

```
android {
    defaultConfig {
        // Enabling multidex support.
        multiDexEnabled true
    }
    dexOptions {
        jumboMode true
        incremental true
        javaMaxHeapSize "4g"
    }
}
```

```
compile 'com.android.support:multidex:1.0.1'
```

## Implementation

### Import

You can use the Voxeet and UserInfo classes using the following :

- `Voxeet` is a singleton from which you will interact with the SDK
- `UserInfo` is a class :
    - constructor : `(externalId, name, avatarUrl)`
    - json() : return the corresponding json

### Init

```
Voxeet.initialize(<your consumer key>, <your secret key>)
.then(() => {
  //if the initialization is ok
})
.catch(err => {
  //in case of error
});
```

### Session opening

```
Voxeet.openSession(<userId>, <name>, <avatarUrl>)
.then(() => {
  //if the session is started
})
.catch(err => {
  //in case of error
});
```

### Start/Stop a conference

You can start a conference with its conferenceId. You can also invite
others to join the current conference by using an array of UserInfo

```
Voxeet.startConference(<conferenceId>, <optional UserInfo[]>)
.then(() => {
  //call made and everything is ok
})
.catch(err => {
  //in case of error
});
```

You can also stop a given conference using the following method which will close the conference.

```
Voxeet.stopConference()
.then(() => {
  //call made and everything is ok
})
.catch(err => {
  //in case of error
});
```

### Stop the session


```
Voxeet.closeSession()
.then(() => {
  //if the session is closed
})
.catch(err => {
  //in case of error
});
```

## Cordova example

```
var user0 = new UserInfo('0', 'Benoit', 'https://cdn.voxeet.com/images/team-benoit-senard.png');
var user1 = new UserInfo('1', 'Stephane', 'https://cdn.voxeet.com/images/team-stephane-giraudie.png');
var user2 = new UserInfo('2', 'Thomas', 'https://cdn.voxeet.com/images/team-thomas.png');

Voxeet.initialize('consumerKey', 'consumerSecret')
.then(() => Voxeet.openSession(user0))
.then(() => Voxeet.startConference('conferenceId', [user1, user2]))
.then(() => alert("done"))
.error(err => alert(err));
```

## Supported Platforms

- iOS (follow these steps to set up Xcode project https://github.com/voxeet/voxeet-ios-conferencekit#project-setup and add `ALWAYS_EMBED_SWIFT_STANDARD_LIBRARIES` in build settings to true)
- Android

## License

This code-source is under the GPL-v3 License
