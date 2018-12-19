Voxeet Cordova Plugin

# cordova-plugin-voxeet

This plugin is bridging the Voxeet's Toolkit/ConferenceKit calls. You can interacting with the plugin after the Cordova's deviceReady event.

It is mandatory that you added:
  - the iOS platform `cordova platform add ios`
  - the Android platform `cordova platform add android`

## Installation

    cordova plugin add https://github.com/voxeet/voxeet-cordova-conferencekit

    By default the postinstall options will try to build the ios package. To skip the postinstall you can set env variable `VOXEET_SKIP_IOS_BUILD` to true. `export VOXEET_SKIP_IOS_BUILD=true`

### iOS

- after `cordova platform add ios` in the project root folder
- set `ALWAYS_EMBED_SWIFT_STANDARD_LIBRARIES` in Xcode build settings to true

To enable push notification, follow https://github.com/voxeet/voxeet-ios-conferencekit#project-setup


### Android

No steps are required

### Notification

To enable push notification, follow the steps in the app, for push notification, follow https://github.com/voxeet/android-sdk-sample

- **Verify that the Voxeet Push Notification services's tags are before the other services registered in the AndroidManifest with the proper priority to prevent Cordova's FCM issues**

- You also need to make a modification in the generated MainActivity. Using Android Studio or any other IDE :

right before the call to `super.onCreate(savedInstanceState);` :
```
getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
);
```

This call use the following import : `import android.view.WindowManager;`

- in order to implement properly the push notification, you also need to register the default accepted view **if you changed its name**. Edit the _AndroidManifest.xml_ file and put this xml node in the _<application></application>_ :
```
<meta-data android:name="rn_voxeet_incoming_call_accepted_or_declined" android:value="fully.qualified.package.name.to.the.default.cordova.activity" />
```

## Implementation

### Import

You can use the Voxeet and UserInfo classes using the following :

- `Voxeet` is a singleton from which you will interact with the SDK
- `UserInfo` is a class :
    - constructor : `(externalId: string, name: string, avatarUrl: string)`
    - json() : return the corresponding json

### initialize without OAuth2

```
Voxeet.initialize(<your consumer key>: string , <your secret key>: string)
.then(() => {
  //if the initialization is ok
})
.catch(err => {
  //in case of error
});
```

### initialize with OAuth2

```
//Voxeet.initializeWithRefresh(accessToken: string , refreshToken: () => Promise<boolean>)

//the callback to be used
const refreshToken = () => {
  return new Promise((resolve, reject) => {
    //here do your network call to get a new accessToken
    //and do resolve(theAccessTokenValue);
  });
}

//the actual call to the SDK initialization
Voxeet.initializeWithRefresh("someValidAccessToken" , refreshToken)
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
