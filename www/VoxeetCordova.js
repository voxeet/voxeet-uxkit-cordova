"use strict";
const VoxeetMedia_1 = require("./VoxeetMedia");
const ConferenceStatus_1 = require("./types/ConferenceStatus");
const UserInfo_1 = require("./UserInfo");
/**
 * This class enable interaction with VoxeetUXKit
 */
const exec = require('cordova/exec');
const SERVICE = 'Voxeet';
class VoxeetSDK {
    constructor() {
        this.refreshAccessTokenCallback = null;
        this.VoxeetMedia = new VoxeetMedia_1.default();
        this.refreshAccessTokenCallback = () => {
            this.refreshToken && this.refreshToken()
                .then(accessToken => this.onAccessTokenOk(accessToken))
                .catch(err => {
                console.log(err);
                this.onAccessTokenKo("Error while refreshing token");
            });
        };
    }
    /**
     * Initializes the SDK using the customer key and secret.
     * @param consumerKey Consumer Key
     * @param consumerSecret Consumer Secret
     */
    initialize(consumerKey, consumerSecret) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'initialize', [consumerKey, consumerSecret]);
        });
    }
    /**
     * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
     * @param accessToken Access token
     * @param refreshToken Callback to get a new access token after it expires
     */
    initializeToken(accessToken, refreshToken) {
        return new Promise((resolve, reject) => {
            this.refreshToken = refreshToken;
            exec(this.refreshAccessTokenCallback, (err) => { }, SERVICE, 'refreshAccessTokenCallback', []);
            exec(resolve, reject, SERVICE, 'initializeToken', [accessToken]);
        });
    }
    /**
     * Opens a new session.
     * @param userInfo Participant information
     */
    connect(userInfo) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'connect', [userInfo.json()]);
        });
    }
    /**
     * Closes the current session.
     */
    disconnect() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'disconnect', []);
        });
    }
    /**
     * Creates a conference.
     * @param options Options to use to create the conference
     */
    create(options) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'create', [options]);
        });
    }
    /**
     * Joins the conference and opens the conference overlay.
     * @param conferenceId Id of the conference to join
     * @param options Options to use to join the conference
     */
    join(conferenceId, options = {}) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'join', [conferenceId, options]);
        });
    }
    /**
     * Joins the conference in the broadcaster mode which allows transmitting audio and video.
     * @param conferenceId Id of the conference to join
     */
    broadcast(conferenceId) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'broadcast', [conferenceId]);
        });
    }
    /**
     * Leaves the conference.
     */
    leave() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'leave', []);
        });
    }
    /**
     * Invite a participant to the conference.
     * @param conferenceId Id of the conference to invite the participant to
     * @param participants List of participants to invite
     */
    invite(conferenceId, participants) {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'invite', [conferenceId, array]);
        });
    }
    /**
     * Sends a broadcast message to the participants of the conference.
     * @param message Message to send to the other participants
     */
    sendBroadcastMessage(message) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'sendBroadcastMessage', [message]);
            resolve();
        });
    }
    /**
     * Sets if you want to enable audio 3D.
     * @param enabled True to enable audio 3D
     */
    setAudio3DEnabled(enabled) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setAudio3DEnabled', [enabled]);
        });
    }
    /**
     * Is audio 3D enabled.
     */
    isAudio3DEnabled() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'isAudio3DEnabled', []);
        });
    }
    /**
     * Sets the UI configuration.
     * @param configuration UI configuration
     */
    setUIConfiguration(configuration) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setUIConfiguration', [configuration]);
        });
    }
    /**
     * Sets if you want to enable the Telecom mode or not.
     * @param enabled True to enable the Telecom mode
     */
    setTelecomMode(enabled) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setTelecomMode', [enabled]);
        });
    }
    /**
     * Is telecom mode enabled.
     */
    isTelecomMode() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'isTelecomMode', []);
        });
    }
    /**
     * Sets if you want the UXKit to appear maximized or not.
     * @param maximized True to have the UXKit to appear maximized
     */
    appearMaximized(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'appearMaximized', [enabled]);
            resolve();
        });
    }
    /**
     * Use the built in speaker by default.
     * @param enable True to use the built in speaker by default
     */
    defaultBuiltInSpeaker(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'defaultBuiltInSpeaker', [enabled]);
            resolve();
        });
    }
    /**
     * Sets the video on by default.
     * @param enable True to turn on the video by default
     */
    defaultVideo(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'defaultVideo', [enabled]);
            resolve();
        });
    }
    /**
     * Starts recording the conference.
     */
    startRecording() {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'startRecording', []);
            resolve();
        });
    }
    /**
     * Stops recording the conference.
     */
    stopRecording() {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'stopRecording', []);
            resolve();
        });
    }
    /**
     * Minimize the overlay.
     */
    minimize() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'minimize', []);
        });
    }
    /**
     * Maximize the overlay.
     */
    maximize() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'maximize', []);
        });
    }
    /**
     * Activates or disable the screen auto lock. Android only.
     * @param activate True to activate the screen auto lock
     */
    screenAutoLock(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'screenAutoLock', [enabled]);
            resolve(null);
        });
    }
    /**
     * @deprecated Android only.
     */
    isUserLoggedIn() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'isUserLoggedIn', []);
        });
    }
    /**
     * Checks if a conference is awaiting. Android only.
     */
    checkForAwaitingConference() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'checkForAwaitingConference', []);
        });
    }
    /**
     * Get notified when the conference status changes.
     * @param callback function to call when the conference status changes.
     * @returns @deprecated to match previous implementations, resolve a promise ; will be return void in the future
     */
    onConferenceStatusUpdatedEvent(callback) {
        exec((object) => {
            const event = ConferenceStatus_1.fromRawToConferenceStatus(object);
            if (null != event)
                callback(event);
            else
                console.log("invalid event received or not cross platform", object);
        }, (err) => { }, SERVICE, 'onConferenceStatusUpdatedEvent', []);
        return Promise.resolve(true);
    }
    /** @deprecated Use join() instead. */
    startConference(conferenceId, participants) {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'startConference', [conferenceId, array]);
        });
    }
    /** @deprecated Use leave() instead. */
    stopConference() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'stopConference', []);
        });
    }
    /** @deprecated use connect instead. */
    openSession(userInfo) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'openSession', [userInfo.json()]);
        });
    }
    /** @deprecated use disconnect instead. */
    closeSession() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'closeSession', []);
        });
    }
    /** Internal method used to refresh tokens. */
    onAccessTokenOk(accessToken) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'onAccessTokenOk', [accessToken]);
        });
    }
    /** Internal method. */
    onAccessTokenKo(errorMessage) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'onAccessTokenKo', [errorMessage]);
        });
    }
}
//restore deprecated { VoxeetSDK, UserInfo, VoxeetMedia } = VoxeetCordova;
//TODO remove on 1.6
VoxeetSDK.VoxeetMedia = VoxeetMedia_1.default;
VoxeetSDK.UserInfo = UserInfo_1.default;
VoxeetSDK.VoxeetSDK = new VoxeetSDK();
module.exports = new VoxeetSDK();
//# sourceMappingURL=VoxeetCordova.js.map