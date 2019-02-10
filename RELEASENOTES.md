# Release Notes

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
