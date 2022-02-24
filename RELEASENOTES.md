# Release Notes

## 1.5.7 (Feb 24th, 2022)
### Features

The Cordova UXKit now uses the iOS Client SDK [3.3.3](https://github.com/voxeet/voxeet-sdk-ios/releases/tag/3.3.3) and iOS Client UXKit [1.6.5](https://github.com/voxeet/voxeet-uxkit-ios/releases/tag/1.6.5).

### Bug Fixes

Fixed an issue where submitting an application resulted in receiving errors informing about nested bundles.

## 1.5.6 (Feb 4th, 2022)
### Features

The Cordova UXKit now uses the iOS Client SDK [3.3.3](https://github.com/voxeet/voxeet-sdk-ios/releases/tag/3.3.3) and iOS Client UXKit [1.6.4](https://github.com/voxeet/voxeet-uxkit-ios/releases/tag/1.6.4).

### Bug Fixes

Fixed an issue where the UXKit fails to compile for iOS due to failing Carthage dependency.

## 1.5.5 (Oct 19th, 2021)
- bump iOS SDK to 3.2.2 and iOS UXKit to 1.5.2

## 1.5.4 (Jul 8th, 2021 & Jul 27th, 2021)
- bump iOS SDK to 3.2.0 and iOS UXKit to 1.5.0
- bump Android SDK to 3.2.1 and Android UXKit to 3.2.1

## 1.5.3 (May 19th, 2021)
- bump iOS SDK to 3.1.6 and iOS UXKit to 1.4.8

## 1.5.3-BETA4 (Mar 12ndn 2021)
- Update definition
- Android change deprecated usage to expected join method
- Android bump to 3.1.1
- voxeet_application.js compatibility with Capacitor apps

## 1.5.3-BETA1 (Mar 8th, 2021)
- Restore possibility to use `VoxeetSDK`, `UserInfo` and `VoxeetMedia` from global variable. Deprecated `1.5.0` subinjection (UserInfo.UserInfo and VoxeetMedia.VoxeetMedia to transpilation)

## 1.5.2 (Mar 5th, 2021)
- Android, fix ConferenceStatusUpdated `status` name

## 1.5.1 (Mar 2nd, 2021)
- bump iOS SDK to 3.1.0 and iOS UXKit to 1.4.2
- add `onConferenceStatusUpdatedEvent` event
- remove 1.5.0 explicit cordova dependency for pure compatibility with forked-environment

## 1.5.0 (Feb 26th, 2021)
- changing main object from the cordova injection from VoxeetSDK to VoxeetCordova
- use `const { VoxeetSDK } = VoxeetCordova` or `const { VoxeetSDK } = window.VoxeetCordova`
- `UserInfo` and all other configuration are part of the `VoxeetCordova` object

## 1.4.9 (Feb 25th, 2021)
- delivery rework to include npm-centric bundle management

## 1.4.8 (Feb 25th, 2021)
- hotfix Android version issue not matching the expected UXKit's version

## 1.4.7 (Feb 9th, 2021)
- bump Android UXKit for minimize/maximize methods
- bump iOS UXKit to 1.4.1 for minimize/maximize methods

## 1.4.7-BETA2 (Dec, 16th, 2020)
 - use UXKit's Contextual configuration for default speaker mode

## 1.4.6 (Dec 15th, 2020)
- bump iOS SDK to 3.0.3

## 1.4.6-BETA1 (Dec)
- use UXKit Beta2
- pre initialize SDK
- fix media device empty list
- improve video rendering (fix video distortion)

## 1.4.5 (Nov 30th, 2020)
- bump Android SDK to 3.0.2

## 1.4.4 (Nov 30th, 2020)
- bump iOS SDK to 3.0.2

## 1.4.3 (Nov 18th, 2020)
- Android, remove code forcing use of incoming call activity even in Android 10+ (Non disturbing mode)

## 1.4.2 (Nov 16th, 2020)
- bump iOS SDK to 3.0.1 to support Swift 5.3.1

## 1.4.1 (Nov 12th, 2020)
- reimplement listener mode on iOS

## 1.4.0 (Nov 3rd, 2020)
- update Android's SDK to 3.0.0
- various fixes added via the UXKit 3.0.0
- Android notifications are now using overhead

## 1.3.35 (Oct 29th, 2020)
- copy Carthage temporary script

## 1.3.34 (Oct 29th, 2020)
- bump iOS SDK to 3.0.0

## 1.3.33 (Oct 13th, 2020)
- bump iOS SDK to 2.4.1 to support Swift 5.3

## 1.3.32 (June 23rd, 2020)
- Android & iOS, integrate 2.4.0

## 1.3.31 (May 25th, 2020)
- bump iOS SDK to 2.3.1 to support Swift 5.2.4

## 1.3.30 (May 18th, 2020)
- Android use of the 2.3.1 native SDK

## 1.3.29 (May 13th, 2020)
- iOS, implement new screen share broadcasting feature

## 1.3.28 (April 17th, 2020)
- bump iOS SDK to 2.2.1 to support Swift 5.2.2

## 1.3.27 (March 31th, 2020)
- Android use of the 2.2.0 SDK
- fix with creation and join's issues where network calls could prevent message forwarding
- less verbose logs for production envs

## 1.3.26 (March 30th, 2020)
- implement new iOS classes for CallKit 

## 1.3.25 (March 30th, 2020)
- bump iOS SDK to 2.2.0

## 1.3.24 (March 26th, 2020)
- bump iOS SDK to 2.1.1 to support Swift 5.2

## 1.3.23 (March 11th, 2020)
- Fix crash in toolkit pre-initialization

## 1.3.22 (March 3rd, 2020)
- bump iOS SDK to 2.1.0

## 1.3.21 (March 3rd, 2020)
- inject Android sound management

## 1.3.20 (February 12th, 2020)
- bump iOS SDK to 2.0.0

## 1.3.18 (December 20th, 2019)
- missing elements for media and keyguard

## 1.3.17 (December 17th, 2019)
- use Carthage binary instead of github for VoxeetSDK dependency

## 1.3.16 (December 12th, 2019)
- remove remaining Firebase's service following deprecation 

## 1.3.15 (December 12th, 2019)
- bump iOS SDK to 1.4.9 to support Swift 5.1.3

## 1.3.14 (December 11th, 2019)
- use Android sdk version with 1.8 support
- CordovaFirebaseMessagingService is now a dummy service for compat wit failed install (refer to auto )
- set blacklist of Android 10 for fullscreen notification
- Voice mode release will not take over the media but will still switch to media mode
- add Nexus 7 support
- add hisi codec support

## 1.3.13 (November 24th, 2019)
- Android's media library with embedded native library
- Android's main SDK and toolkit upgrade
- use of toolkit-firebase instead of previous push library when push notification are enabled

## 1.3.12 (November 19th, 2019)
- bump iOS SDK to 1.4.8 to support Swift 5.1.2
- Android, remove dismiss keyguard on incoming calls
- add 1.3.13 as well (file movement)

## 1.3.11 (October 30th, 2019)
- if the current app is opened on Android 10, start the incoming call

## 1.3.10 (October 29~30th, 2019)
- update notification management with proxy (for now)
- 1.3.9 should not be used (issue with the package)

## 1.3.8 (October 28th, 2019)
- use sdk and uxkit embedding new push center management

## 1.3.7 (October 24th, 2019)
- Android, mic permission of accept calls
- Improve events forwarding onto proper element
- add INTERACT_ACROSS_USERS permission following evolution into new Samsung's Android 8 build (breaking change from old INTERACT_ACROSS_USERS_FULL prior to those builds)

## 1.3.6 (October 23th, 2019)
- Android, improved incoming call for metadata + parameters conjunction

## 1.3.5 (October 21th, 2019)
- Android, accept incoming calls for empty avatar urls

## 1.3.4 (October 18th, 2019)
- Android, improve initialize with possibility of statically init the SDK before hand

## 1.3.3 (October 18th, 2019)
- Android, on accepted incoming call, if the sdk is already initialized and an user was known, also connect the call

## 1.3.2 (October 17th, 2019)
- Android, use suggested google play services built in
- renaming of the stream method into music for more context and accuracy
- replace possible unaccurate and mixed state of the onResume/onPause for call/music discrepancy

## 1.3.1 (October 17th, 2019)
- copy intercom way of using config.xml inside build.gradle

## 1.3 (October 16th, 2019)
- plugin variable properly used to initialize the SDK on Cordova's warmup

## 1.2.18 (October 16th, 2019)
- Protect iOS initialize method to handle both config.xml and js init at the same time

## 1.2.17 (October 16th, 2019)
- Android & iOS, use initialize with onload

## 1.2.16 (October 14th, 2019)
- iOS, bump version and dependencies

## 1.2.15 (October 8th, 2019)
- iOS, crash fixed in stop video for iOS 13
- Android use improved internal SDK revision with light support
- Android, less push configuration required (favor of metadata only)
- Android, multiple init won't collide and reject promise (similar to iOS behaviour)

## 1.2.16 (October 14th, 2019)
- bump iOS Voxeet SDKs to fix crash issues

## 1.2.15 (October 8th, 2019)
- use updated sdk (video stream etc...)/register event for push in invite

## 1.2.14 (October 1st, 2019)
- bump iOS Voxeet SDKs for Swift 5.1

## 1.2.13 (September 17th, 2019)
  - bump revision to reflect unsent service file for Android

## 1.2.12 (September 11th, 2019)
  - Android, add plugin.xml info such as standard push notification helper
  - add as well default messaging service

## 1.2.11 (August 22th, 2019)
  - reset the script to be used in the uploaded package

## 1.2.10 (August 22th, 2019)
  - iOS install script releasing hold on promise
  - set the shell script in the uploaded package for quick retro compat

## 1.2.9 (August 21th, 2019)
  - Android, updated SDK to reflect fix for push notification not internally propagated
  - Android, change Cordova's implementation according to the new SDK version

## 1.2.0 (May 21th, 2019)
  - lock ios versions
  - add getter for audio and telecom mode

## 1.1.7 (May 21th, 2019)
  - fix typo from 1.1.6 update
  - remove typescript duplicates

## 1.1.6 (May 20th, 2019)
  - audio3D disabled by default
  - add hybrid accessor for telecom mode and audio3D access

## 1.1.4 (April 15th, 2019)
  - fix mirror mode in Android
  - fix listener mode on iOS
  - improved native promises management in Android

## 1.1.3 (April 15th, 2019)
  - Android, use updated native toolkit

## 1.1.2 (April 12th, 2019)
  - Add broadcasting mode

## 1.1.1 (March 28th, 2019)
  - Bump iOS frameworks versions for Swift 5

## 1.1 (March 20th, 2019)
  - Android, fix conferenceAlias -> alias
  - iOS implementation of listener mode
  - reset ability to use directly the repository
  - breaking change in the importation : [README](./README.md)

## 1.0.36 (March 19th, 2019)
  - Android, use upgraded SDK with WebRTC 72 support
  - repo architecture now supports typescript
  - push version into the repositories of npmjs

## 1.0.35 (March 18th, 2019)
  - Android add pre-listener version
  - Android upgrade to the 1.4.18.1 sdk version

## 1.0.34 (March 7th, 2019)
  - keyboard hides on expand
  - use internal create/join improvements

## 1.0.33 (March 6th, 2019)
  - use Android toolkit with proper avatar/usernames UX
  - Bluetooth devices do not trigger and force BT route

## 1.0.32 (March 3rd, 2019)
  - Android, use 1.4.14 SDK with enhanced management
  - fix audio routes -> 'in call' to 'in communication'
  - fix grasp on routes
  - improved overlay actions
  - action on users in the overlay can only be done if they are on air
  - auto detect notch
  - prevent usernames to impact the UI
  - remove keyboard when the overlay appears

## 1.0.31 (February 24th, 2019)
  - Android, use 1.4.11 SDK with enhanced BLUETOOTH route
  - Fix issue on Android where calls were being trigerred on resume/pause states
  - Being on a call will prevent screen fadding away on Android

## 1.0.30 (February 18th, 2019)
  - Android, hardened SDK calls with check for initialization call before hand

## 1.0.29 (February 10th, 2019)
  - Android, used upgraded SDK version with preferences saved accross (prevent miss configuration)
  - if set by default, start video for accepted calls

## 1.0.28 (January 30th, 2019)
  - Android, wider range of devices with proper phone/media audio management

## 1.0.27 (January 29th, 2019)
  - (Android :)
  - fix volume implementation
  - users wrongly displayed are now properly displayed
  - joining a conference with video by default now works in the create->join
  - screen on / proximity sensors work by default
  - mic permission is managed prior joining a conference
  - removed default timeout

## 1.0.26 (January 23th, 2019)
  - add possible USE_PUSH_NOTIFICATION system variable to enable Firebase in the Android compilation

## 1.0.25 (January 21th, 2019)
  - use Android sdk (1.4) with AEC and NS by default
  - toolkit now use the overlay properly everytime

## 1.0.24 (January 16th, 2019)
  - use Android sdk update with better support of connectivity

## 1.0.23 (December 13th, 2018)
  - use new Android SDK with improved sockets, flow and fix for Android 4.+

## 1.0.22 (November 30th, 2018)
  - new incoming call management

## 1.0.21 (November 5th, 2018)
  - add new methods (create, join, invite, leave, defaultVideo)

## 1.0.20 (October 27th, 2018)
  - upgrade the Android SDK (better connectivity support, better ui/ux)

## 1.0.19 (October 25th, 2018)
  - upgrade media to match the VU Meter

## 1.0.18 (October 25th, 2018)
  - fix crash on sound type

## 1.0.17 (October 23th, 2018)
  - add ring sound in incoming call activity (Android)

## 1.0.16 (October 23th, 2018)
  - use upgraded SDK (Android)
  - better look and feel for Android, now same look&feel accross OS (Android)

## 1.0.15 (October 3rd, 2018)
  - add auto close incoming close incoming call (default 40s)

## 1.0.14 (October 3rd, 2018)
  - add notification channel management into SDK

## 1.0.13 (October 1st, 2018)
  - add methods and logic to automatically manage the anti native workflow

## 1.0.12 (September 28th, 2018)
  - fix missing call to the FCM Controller to enable push/log

## 1.0.11 (September 21th, 2018)
  - add unlock keyguard

## 1.0.10 (September 20th, 2018)
  - fix the multiple view in Android (config.xml => `<preference name="AndroidLaunchMode" value="singleInstance" />`)

## 1.0.9 (September 19th, 2018)
  - upgrade Android package with fewer dependencies and fix a possible multiple conference views (with black overlays)

## 1.0.8 (September 18th, 2018)
  - fix NPE in Android preventing FCM functionnality (1.1.8.7)

## 1.0.7 (September 18th, 2018)
  - upgrade the Android SDK version to 1.1.8.6

## 1.0.6 (July 27th, 2018)
  - add broadcast messages support for android

## 1.0.5 (May 29th, 2018)
  - update documentation

## 1.0.2 (May 28th, 2018)
  - documentation and npm package updated

## 1.0.0 (May 28th, 2018)
  - first iOS/Android stable version

## 0.0.1 (May 18th, 2018)
  - initial version
