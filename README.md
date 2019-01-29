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

To enable VoIP notification, follow https://github.com/voxeet/voxeet-ios-conferencekit#project-setup

### Android

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

### Initialize

```
Voxeet.initialize(<consumer_key>: string , <secret_key>: string)
.then(() => {
  //if the initialization is ok
})
.catch(err => {
  //in case of error
});
```

### Initialize with OAuth2

```
Voxeet.initializeWithRefresh(accessToken: string , refreshToken: () => Promise<boolean>)

//the refresh token callback to be used
const refreshToken = () => {
    return new Promise((resolve, reject) => {
    cordovaHTTP.get("https://YOUR_REFRESH_TOKEN_URL", null, {}, function(response) {
      var json = undefined;
      try { json = JSON.parse(response.data); } catch(e) { json = response; };
      resolve(json.access_token) //return your access token
    }, function(response) {
      alert('error ' + response.error);
    });
  });
}

//the actual call to the SDK initialization
refreshToken()
.then(result => Voxeet.initializeToken(result, refreshToken))
.then(() => {
  //if the initialization is ok
})
.catch(err => {
  //in case of error
});
```

### Connect/disconnect a session

```
var user = new UserInfo(<externalId>: string, <name>: string, <avatarUrl>: string);

Voxeet.connect(user)
.then(() => {
  //if the session is connected
})
.catch(err => {
  //in case of error
});
```

```
Voxeet.disconnect()
.then(() => {
  //if the session is disconnected
})
.catch(err => {
  //in case of error
});
```

### Create/join/invite/leave a conference

You can create a conference with a custom alias (optional). You can also invite others to join the current conference by using the `invite` method.

```
var user1 = new UserInfo(<externalId>: string, <name>: string, <avatarUrl>: string);
var user2 = new UserInfo(<externalId>: string, <name>: string, <avatarUrl>: string);
var user3 = new UserInfo(<externalId>: string, <name>: string, <avatarUrl>: string);

Voxeet.create({conferenceAlias: 'YOUR_CONFERENCE_ALIAS'})
.then(result => Promise.all([
  Voxeet.join(result.conferenceId),
  result.isNew ? Voxeet.invite(result.conferenceId, [user1, user2, user3]) : null
]))
.catch(err => {
  //in case of error
});
```

An other example if you don't want to invite anyone and a conference alias isn't needed.

```
Voxeet.create({})
.then(result => Promise.all([
  Voxeet.join(result.conferenceId),
]))
.catch(err => {
  //in case of error
});
```

You can also stop a given conference using the following method which will leave the conference.

```
Voxeet.leave()
.then(() => {
  //call made and everything is ok
})
.catch(err => {
  //in case of error
});
```

### Broadcast a message

```
Voxeet.sendBroadcastMessage('YOUR_MESSAGE')
.then(() => {
  //message sent
})
.catch(err => {
  //error while sending the message
});
```

### Useful methods

By default, conference appears maximized. If false, the conference will appear minimized.

```
Voxeet.appearMaximized(true)
```

By default, conference starts on the built in receiver. If true, it will start on the built in speaker.

```
Voxeet.defaultBuiltInSpeaker(true)
```

By default, conference starts without video. If true, it will enable the video at conference start.

```
Voxeet.defaultVideo(true)
```

## Cordova example

```
var user1 = new UserInfo('0', 'Benoit', 'https://cdn.voxeet.com/images/team-benoit-senard.png');
var user2 = new UserInfo('1', 'Stephane', 'https://cdn.voxeet.com/images/team-stephane-giraudie.png');
var user3 = new UserInfo('2', 'Thomas', 'https://cdn.voxeet.com/images/team-thomas.png');

Voxeet.initialize('YOUR_CONSUMER_KEY', 'YOUR_CONSUMER_SECRET')
.then(() => Voxeet.appearMaximized(true))
.then(() => Voxeet.defaultBuiltInSpeaker(true))
.then(() => Voxeet.defaultVideo(true))
.then(() => Voxeet.connect(user1))
.then(() => Voxeet.create({conferenceAlias: 'YOUR_CONFERENCE_ALIAS', params: {videoCodec: 'H264'}}))
.then(result => Promise.all([
  Voxeet.join(result.conferenceId),
  result.isNew ? Voxeet.invite(result.conferenceId, [user2, user3]) : null
]))
.then(() => Voxeet.sendBroadcastMessage('Hello world'))
.then(() => Voxeet.leave())
.then(() => Voxeet.disconnect())
.then(() => alert("done"))
.error(err => alert(err));
```

## Supported Platforms

- iOS
- Android

## License

```
   Copyright 2019 - Voxeet

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
