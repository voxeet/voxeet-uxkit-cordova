# Requirements

- Install Cordova tools : https://cordova.apache.org/docs/en/8.x/guide/cli/index.html
- Configure your app at [https://developer.voxeet.com](The developer portal) Voxeet CustomerKey @ CustomerSecret
- Choose a package name for example: `com.voxeet.cordova.receiver`
- Firebase configuration :
  - Create a project
  - Add an app with the package name
  - Download the google-services.json
  - Copy the Server API key and paste it to your application in the Voxeet developer portal

# Create the Cordova App

## Initialize a minimalistic application

From a terminal, run :
```
cordova create receiver com.voxeet.cordova.receiver CordovaReceiver
```

And for the Caller :
```
cordova create sender com.voxeet.cordova.sender CordovaSender
```

**Note : The below configurations must be done for each project !**

## Add Android platform

From a terminal, run :
  - `cd receiver`
  - `cordova platform add android`

Do the same for the `sender` app :
  - `cd sender`
  - `cordova platform add android`


## Edit the configuration

The Firebase console should have generated a `google-services.json` file. Put the `google-services.json` file in `platforms/android/app/` folder.

Then edit the `config.xml` to put the below configuration : (you must do it per project)

```
<preference name="AndroidLaunchMode" value="singleInstance" />
```

## Add Voxeet Plugin

From the terminal, run the following commands :

```
cordova plugin add https://github.com/voxeet/voxeet-cordova-conferencekit
```

## Add Firebase

Run the command :
```
cordova plugin add phonegap-plugin-push
```

And edit the file `platforms/android/app/src/main/AndroidManifest.xml` (or `platforms/android/src/AndroidManifest.xml` in older cordova versions) to add the following tags in the *<application />* :

```
<service android:name="voxeet.com.sdk.firebase.VoxeetFirebaseMessagingService">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
<service android:name="voxeet.com.sdk.firebase.VoxeetFirebaseInstanceIDService">
    <intent-filter>
        <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
    </intent-filter>
</service>
```

Check that you have `<variable name="FCM_VERSION" value="11.6.2" />` in your `config.xml`

## Check for non regression

Now that the plugin is installed and ready to be used in your code, check that the app still runs :

```
rm -rf platforms/android/build platforms/android/*/build
cordova clean
cordova run android
```

# Minimalist Voxeet Code


## For the Receiver

In the file `www/js/index.js` in the onDeviceReady method add:

```
const id = "4444";
const name = "Receiver";
Voxeet.initialize("YourKeys", "YourSecret")
.then(result => Voxeet.openSession(new UserInfo(id, name, "https://cdn.voxeet.com/images/team-stephane-giraudie.png")))
.then(result => alert("session opened") )
.catch(err => alert(err) );
```

## For the Sender

In the file `www/js/index.js` in the onDeviceReady mathod add:

```
//set the current user metadata
const id = "5554";
const name = "Sender";
const avatarUrl = "https://cdn.voxeet.com/images/team-stephane-giraudie.png";

//init -> login -> create -> join
Voxeet.initialize("YourKeys", "YourSecret")
.then(result => Voxeet.openSession(new UserInfo(id, name, avatarURL)))
.then(() => Voxeet.create({ conferenceAlias: "conferenceAlias" }))
.then(result => Voxeet.join(result.conferenceId))
.then(result => console.log("conference started"))
.catch(err => alert(err) );
```

## Test

Use `cordova run android` to test the app.
