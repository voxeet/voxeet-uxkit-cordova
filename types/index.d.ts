// Type definitions for Cordova Voxeet plugin
// Project: https://github.com/voxeet/voxeet-cordova-conferencekit
// Definitions by: Microsoft Open Technologies Inc <http://msopentech.com>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped
//
// Copyright (c) Microsoft Open Technologies Inc
// Licensed under the MIT license.

interface Voxeet {

    /**
     * Initialize the SDKs
     * @param consumerKey a valid not null, undefined key
     * @param consumerSecret a valid not null, undefined key
     * @return a promise which will resolve if success or reject if failed
     */
    initialize(consumerKey: string, consumerSecret: string): Promise;

    /**
     * Open a session for a given user
     * @param userInfo a valid not null, undefined UserInfo object
     * @return a promise which will resolve if success or reject if failed
     */
    openSession(userInfo: UserInfo): Promise;

    /**
     * Close the session for the connected user
     * @return a promise which will resolve if it was successfully done or reject
     */
    closeSession(): Promise;

    /**
     * Start a conference
     * @param conferenceId a valid not null conferenceId ("" is invalid)
     * @param participants (optional) a list of valid UserInfo to invite
     * @return a promise which will resolve if success or reject otherwise
     */
    startConference(conferenceId: string, participants: UserInfo[]): Promise;

    /**
     * Stop the current conference
     * @return a promise which will resolve if success or reject otherwise
     */
    stopConference(): Promise;

    /**
     * Add a user to the conference
     * Since it is managed in the SDK, the functionnality is not available for now
     */
    add(/* participant */): Promise;

    /**
     * Update a user in the conference
     * Since it is managed in the SDK, the functionnality is not available for now
     */
    update(/* participant */): Promise;

    /**
     * Remove a user from the conference
     * Since it is managed in the SDK, the functionnality is not available for now
     */
    remove(/* participant */): Promise;

    /**
     * Change the default state of the conference overlay
     * by default : true (expanded)
     */
    appearMaximized(enabled: bool): Promise;

    /**
     * Set the speaker by default
     */
    defaultBuiltInSpeaker(enabled: bool): Promise;

    /**
     * Dis/-able the screen autolock
     */
    screenAutoLock(enabled: bool): Promise;
}

interface UserInfo {
    /**
     * @return a valid json with name, externalId, avatarUrl keys
     */
    json(): Object;
}
