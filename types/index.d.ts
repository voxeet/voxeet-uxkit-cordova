// Type definitions for Cordova Voxeet plugin
// Project: https://github.com/voxeet/voxeet-cordova-conferencekit
// Definitions by: Microsoft Open Technologies Inc <http://msopentech.com>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped
//
// Copyright (c) Microsoft Open Technologies Inc
// Licensed under the MIT license.

interface Voxeet {

    initialize(consumerKey: string, consumerSecret: string): Promise;

    openSession(userId: string, participantName: string, avatarUrl: string): Promise;

    closeSession(): Promise;

    startConference(conferenceId: string, participants: UserInfo[]): Promise;

    stopConference(): Promise;

    add(/* participant */): Promise;

    update(/* participant */): Promise;

    remove(/* participant */): Promise;

    appearMaximized(enabled: bool): Promise;

    defaultBuiltInSpeaker(enabled: bool): Promise;

    screenAutoLock(enabled: bool): Promise;
}

interface UserInfo {
    json(): Object;
}
