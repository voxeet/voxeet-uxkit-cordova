Voxeet Cordova Plugin

# cordova-plugin-voxeet

This plugin is bridging the Voxeet's Toolkit/ConferenceKit calls. You can interacting with the plugin after the Cordova's deviceReady event.

It is mandatory that you added:
  - the iOS platform `cordova platform add ios`
  - the Android platform `cordova platform add android`

## Installation

    cordova plugin add @voxeet/cordova-plugin-voxeet@1.0.13

### iOS

- after `cordova platform add ios` in the project root folder
- set `ALWAYS_EMBED_SWIFT_STANDARD_LIBRARIES` in Xcode build settings to true

To enable push notification, follow https://github.com/voxeet/voxeet-ios-conferencekit#project-setup

### Android

- after `cordova platform add android` in the project root folder
- edit the `platforms/android/app/build.gradle` with:

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

- as well as the dependencies block, put in the top dependency position:
```
compile 'com.android.support:multidex:1.0.3'
```

### Notification

To enable push notification, follow the steps in the app, for push notification, follow https://github.com/voxeet/android-sdk-sample

You also need to make a modification in the generated MainActivity. Using Android Studio or any other IDE :

right before the call to `super.onCreate(savedInstanceState);` :
```
getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
);
```

This call use the following import : `import android.view.WindowManager;`

## Implementation

### Import

You can use the Voxeet and UserInfo classes using the following :

- `Voxeet` is a singleton from which you will interact with the SDK
- `UserInfo` is a class :
    - constructor : `(externalId: string, name: string, avatarUrl: string)`
    - json() : return the corresponding json

### Init

```
Voxeet.initialize(<your consumer key>: string , <your secret key>: string)
.then(() => {
  //if the initialization is ok
})
.catch(err => {
  //in case of error
});
```

### Session opening

```
Voxeet.openSession(<userId>: string, <name>: string, <avatarUrl>: string)
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
Voxeet.startConference(<conferenceId>: string, <optional participants>: UserInfo[])
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

### Broadcast a message


```
Voxeet.sendBroadcastMessage(your_message)
.then(() => {
  //message sent
})
.catch(err => {
  //error while sending the message
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

- iOS
- Android

## License

This code-source is under the GPL-v3 License
