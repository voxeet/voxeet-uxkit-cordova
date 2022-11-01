# Voxeet UXKit Cordova

## SDK License agreement

Before using the UXKit for Cordova, please review and accept the [Dolby Software License Agreement](SDK_LICENSE.md).

## Release Notes

Read the [release notes](RELEASENOTES.md) for this project.

## Installation

Before installation, add the iOS and/or Android platforms to your Cordova application. Open a terminal in the `src-cordova` folder and run the following commands:

```bash
cordova platform add ios
cordova platform add android
```

Install the UXKit for Cordova using the following command:

```bash
cordova plugin add cordova-plugin-voxeet
```

**Note:** In some cases, you must initialize the SDK beforehand. Especially in cases when the plugin used by the application delays the `onDeviceReady` event. In this situation, install the UXKit for Cordova using this command:

```bash
cordova plugin add cordova-plugin-voxeet \
    --variable VOXEET_CORDOVA_CONSUMER_KEY="consumerKey" \
    --variable VOXEET_CORDOVA_CONSUMER_SECRET="consumerSecret"
```

> **Note:** Wait for the `deviceReady` event before you interact with the plugin.

### Installation on iOS

1. Make sure that the cordova platform add ios is in the project root folder.
1. In Xcode, set the value of the ALWAYS_EMBED_SWIFT_STANDARD_LIBRARIES build settings to true.
1. To enable VoIP notification, follow the [Swift UXKit guide](https://dolby.io/developers/interactivity-apis/client-ux-kit/uxkit-voxeet-swift).

### Installation on Android

Edit your config.xml to add the following :

```xml
    <platform name="android">
        ...

        <config-file target="AndroidManifest.xml" parent="/manifest/application">
            <meta-data
                android:name="voxeet_incoming_accepted_class"
                android:value="fully.qualified.package.name.to.the.default.cordova.activity" />
        </config-file>

        <edit-config
            file="app/src/main/AndroidManifest.xml"
            target="/manifest/application/activity[@android:name='MainActivity']"
            mode="merge">
            <activity android:exported="true" android:enabled="true" />
        </edit-config>
    </platform>
```

To enable the push notifications in your Android application, add the following preference at the end of the `config.xml` file:

```xml
<widget>
    <!-- Add this line at the end of the file -->
    <preference name="VOXEET_CORDOVA_USE_PUSH" value="true" />
</widget>
```

## Implementation

After installation, the project exports all the elements to integrate them into your application. They are directly injected inside the global variables of your project; you can directly call the `VoxeetSDK` instance and the `UserInfo` constructor whenever needed.

```javascript
//somewhere in your code

alert(`VoxeetSDK ? ${!!VoxeetSDK}`);
alert(`UserInfo ? ${!!UserInfo}`);
```

If you want to use the window object, you can use the following code to access the Voxeet singleton:

```javascript
const { VoxeetSDK, UserInfo } = window;

alert(`VoxeetSDK ? ${!!VoxeetSDK}`);
alert(`UserInfo ? ${!!UserInfo}`);
```

The `VoxeetSDK` object is a singleton that enables interaction with the UXKit. The `UserInfo` class allows you to manipulate participants.

### Initialization

**Initialize the Voxeet UXKit with an Access Token**

```javascript
const tokenUrl = 'https://your-url/token';

// Request the initial access token
fetch(tokenUrl)
    // Parse the response into a JSON object {"access_token": "value"}
    .then(data => data.json())
    .then(json => {
        VoxeetSDK.initializeToken(
            // Initial access token
            json.access_token,
            // Callback to refresh the access token
            () => fetch(tokenUrl)
                .then(data => data.json())
                .then(json => json.access_token)
                .catch(err => {
                    // There was an error
                    console.log(err);
                })
        )
        .then(() => {
            // Initialization is OK
        })
        .catch(err => {
            // There was an error
            console.log(err);
        });
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Initialize the Voxeet UXKit using the Consumer Key and Consumer Secret**

> **WARNING:** It is a best practice to use the `VoxeetSDK.initializeToken` function to initialize the SDK.  
> Please see the [Initializing](https://dolby.io/developers/interactivity-apis/client-sdk/initializing) document.

```javascript
const consumerKey = "CONSUMER_KEY";
const consumerSecret = "CONSUMER_SECRET";

VoxeetSDK.initialize(consumerKey, consumerSecret)
    .then(() => {
        // Initialization is ok
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Connect a session**

```javascript
// Create a UserInfo object
const avatarUrl = "https://gravatar.com/avatar/" + Math.floor(Math.random() * 1000000) + "?s=200&d=identicon";
var user = new UserInfo("externalId", "Guest 01", avatarUrl);

VoxeetSDK.connect(user)
    .then(() => {
        // The session is connected
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Disconnect a session**

```javascript
VoxeetSDK.disconnect()
    .then(() => {
        // The session is disconnected
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Create and join a conference**

```javascript
// Create the conference 'conference_alias' with Dolby Voice on
const createOptions = {
    alias: 'conference_alias',
    params: {
        dolbyVoice: true
    }
};

VoxeetSDK.create(createOptions)
    .then(conf => {
        VoxeetSDK.join(conf.conferenceId)
            .then(() => {
                // You've joined the conference
            })
            .catch(err => {
                // There was an error
                console.log(err);
            });
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Invite users to a conference**

```javascript
const user1 = new UserInfo("external_user_01", "Guest 01");
const user2 = new UserInfo("external_user_02", "Guest 02");
const user3 = new UserInfo("external_user_03", "Guest 03");

VoxeetSDK.invite(conferenceId, [user1, user2, user3])
    .then(() => {
        // The users have been invited
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Leave the conference**

```javascript
VoxeetSDK.leave()
    .then(() => {
        // You have left the conference
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Broadcast a message**

Send a message to the participants of the conference. The message can be a simple string or a json message.

```javascript
var message = "YOUR MESSAGE";

// Example of a json message
var obj = { action: "message", content: "Hello world" };
var message = JSON.stringify(obj);

VoxeetSDK.sendBroadcastMessage(message)
    .then(() => {
        // The message was sent to the participants of the conference
    })
    .catch(err => {
        // There was an error
        console.log(err);
    });
```

**Useful methods**

To maximize or minimize a conference, use the `appearMaximized` method. By default, the conference appears maximized. Change the value to false if you wish to minimize it.

```javascript
await VoxeetSDK.appearMaximized(false);
```

To start a conference using a built-in receiver or a built-in receiver speaker, use the `defaultBuiltInSpeaker` method (as in the example below). By default, the conference starts using a built-in receiver. Change the value to true if you wish to use a built-in speaker.

```javascript
await VoxeetSDK.defaultBuiltInSpeaker(true);
```

To enable a video, use the `defaultVideo` method (as in the example below). By default, the conference starts without a video. Change the value to true if you wish to enable video conferencing.

```javascript
await VoxeetSDK.defaultVideo(true);
```

### Events

**ConferenceStatusUpdated**

To receive events regarding the status of the local `Conference`. This method will be refactored in a future version to provide more events in a consistent and coherent way.

```javascript
const callback = (event: ConferenceStatusUpdated) => {
    const { conferenceId, conferenceAlias, status } = event;
    console.warn(`${conferenceId}/${conferenceAlias} := ${status}`);
};

await VoxeetSDK.onConferenceStatusUpdatedEvent(callback);
```
