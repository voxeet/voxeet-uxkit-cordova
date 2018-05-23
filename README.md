---
title: Voxeet Cordova
description: Interact with the Voxeet ConferenceKit
---
<!--
# license: Licensed to the Apache Software Foundation (ASF) under one
#         or more contributor license agreements.  See the NOTICE file
#         distributed with this work for additional information
#         regarding copyright ownership.  The ASF licenses this file
#         to you under the Apache License, Version 2.0 (the
#         "License"); you may not use this file except in compliance
#         with the License.  You may obtain a copy of the License at
#
#           http://www.apache.org/licenses/LICENSE-2.0
#
#         Unless required by applicable law or agreed to in writing,
#         software distributed under the License is distributed on an
#         "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#         KIND, either express or implied.  See the License for the
#         specific language governing permissions and limitations
#         under the License.
-->

# cordova-plugin-voxeet

This plugin is bridging the Voxeet's Toolkit/ConferenceKit calls. You can interacting with the plugin after the Cordova's deviceReady event.

## Installation

    cordova plugin add cordova-plugin-voxeet

## Implementation

### Import

You can use the Voxeet and UserInfo classes using the following :

```
const Voxeet = navigator.Voxeet;
const UserInfo = navigator.UserInfo;
```

- Voxeet is a singleton from which you will interact with the SDK
- UserInfo is a class :
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


## Supported Platforms

- Android

## Template

Template from Apache Software Foundation
