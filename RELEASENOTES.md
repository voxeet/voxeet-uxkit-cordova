# Release Notes

### 1.1 (March 20th, 2019)
  - Android, fix conferenceAlias -> alias
  - iOS implementation of listener mode
  - reset ability to use directly the repository
  - breaking change in the importation : [README](./README.md)

### 1.0.36 (March 19th, 2019)
  - Android, use upgraded SDK with WebRTC 72 support
  - repo architecture now supports typescript
  - push version into the repositories of npmjs

### 1.0.35 (March 18th, 2019)
  - Android add pre-listener version
  - Android upgrade to the 1.4.18.1 sdk version

### 1.0.34 (March 7th, 2019)
  - keyboard hides on expand
  - use internal create/join improvements

### 1.0.33 (March 6th, 2019)
  - use Android toolkit with proper avatar/usernames UX
  - Bluetooth devices do not trigger and force BT route

### 1.0.32 (March 3rd, 2019)
  - Android, use 1.4.14 SDK with enhanced management
  - fix audio routes -> 'in call' to 'in communication'
  - fix grasp on routes
  - improved overlay actions
  - action on users in the overlay can only be done if they are on air
  - auto detect notch
  - prevent usernames to impact the UI
  - remove keyboard when the overlay appears

### 1.0.31 (February 24th, 2019)
  - Android, use 1.4.11 SDK with enhanced BLUETOOTH route
  - Fix issue on Android where calls were being trigerred on resume/pause states
  - Being on a call will prevent screen fadding away on Android

### 1.0.30 (February 18th, 2019)
  - Android, hardened SDK calls with check for initialization call before hand

### 1.0.29 (February 10th, 2019)
  - Android, used upgraded SDK version with preferences saved accross (prevent miss configuration)
  - if set by default, start video for accepted calls

### 1.0.28 (January 30th, 2019)
  - Android, wider range of devices with proper phone/media audio management

### 1.0.27 (January 29th, 2019)
  - (Android :)
  - fix volume implementation
  - users wrongly displayed are now properly displayed
  - joining a conference with video by default now works in the create->join
  - screen on / proximity sensors work by default
  - mic permission is managed prior joining a conference
  - removed default timeout

### 1.0.26 (January 23th, 2019)
  - add possible USE_PUSH_NOTIFICATION system variable to enable Firebase in the Android compilation

### 1.0.25 (January 21th, 2019)
  - use Android sdk (1.4) with AEC and NS by default
  - toolkit now use the overlay properly everytime

### 1.0.24 (January 16th, 2019)
  - use Android sdk update with better support of connectivity

### 1.0.23 (December 13th, 2018)
  - use new Android SDK with improved sockets, flow and fix for Android 4.+

### 1.0.22 (November 30th, 2018)
  - new incoming call management

### 1.0.21 (November 5th, 2018)
  - add new methods (create, join, invite, leave, defaultVideo)

### 1.0.20 (October 27th, 2018)
  - upgrade the Android SDK (better connectivity support, better ui/ux)

### 1.0.19 (October 25th, 2018)
  - upgrade media to match the VU Meter

### 1.0.18 (October 25th, 2018)
  - fix crash on sound type

### 1.0.17 (October 23th, 2018)
  - add ring sound in incoming call activity (Android)

### 1.0.16 (October 23th, 2018)
  - use upgraded SDK (Android)
  - better look and feel for Android, now same look&feel accross OS (Android)

### 1.0.15 (October 3rd, 2018)
  - add auto close incoming close incoming call (default 40s)

### 1.0.14 (October 3rd, 2018)
  - add notification channel management into SDK

### 1.0.13 (October 1st, 2018)
  - add methods and logic to automatically manage the anti native workflow

### 1.0.12 (September 28th, 2018)
  - fix missing call to the FCM Controller to enable push/log

### 1.0.11 (September 21th, 2018)
  - add unlock keyguard

### 1.0.10 (September 20th, 2018)
  - fix the multiple view in Android (config.xml => `<preference name="AndroidLaunchMode" value="singleInstance" />`)

### 1.0.9 (September 19th, 2018)
  - upgrade Android package with fewer dependencies and fix a possible multiple conference views (with black overlays)

### 1.0.8 (September 18th, 2018)
  - fix NPE in Android preventing FCM functionnality (1.1.8.7)

### 1.0.7 (September 18th, 2018)
  - upgrade the Android SDK version to 1.1.8.6

### 1.0.6 (July 27th, 2018)
  - add broadcast messages support for android

### 1.0.5 (May 29th, 2018)
  - update documentation

### 1.0.2 (May 28th, 2018)
  - documentation and npm package updated

### 1.0.0 (May 28th, 2018)
  - first iOS/Android stable version

### 0.0.1 (Mai 18th, 2018)
  - initial version
