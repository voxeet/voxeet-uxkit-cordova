"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const VoxeetMedia_1 = __importDefault(require("./VoxeetMedia"));
/*
 *
 * VoxeetUXKit Cordova
 * Copyright (C) 2020
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/
/**
 * This class enable interaction with VoxeetUXKit
 * @constructor
 */
const exec = require('cordova/exec');
const SERVICE = 'Voxeet';
var RTCPMode;
(function (RTCPMode) {
    RTCPMode["WORST"] = "worst";
    RTCPMode["BEST"] = "best";
})(RTCPMode = exports.RTCPMode || (exports.RTCPMode = {}));
var Mode;
(function (Mode) {
    Mode["STANDARD"] = "standard";
    Mode["PUSH"] = "push";
})(Mode = exports.Mode || (exports.Mode = {}));
var Codec;
(function (Codec) {
    Codec["VP8"] = "VP8";
    Codec["H264"] = "H264";
})(Codec = exports.Codec || (exports.Codec = {}));
var UserType;
(function (UserType) {
    UserType["USER"] = "user";
    UserType["LISTENER"] = "listener";
})(UserType = exports.UserType || (exports.UserType = {}));
;
;
class Voxeet_ {
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
    initialize(consumerKey, consumerSecret) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'initialize', [consumerKey, consumerSecret]);
        });
    }
    initializeToken(accessToken, refreshToken) {
        return new Promise((resolve, reject) => {
            this.refreshToken = refreshToken;
            exec(this.refreshAccessTokenCallback, (err) => { }, SERVICE, 'refreshAccessTokenCallback', []);
            exec(resolve, reject, SERVICE, 'initializeToken', [accessToken]);
        });
    }
    connect(userInfo) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'connect', [userInfo.json()]);
        });
    }
    disconnect() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'disconnect', []);
        });
    }
    create(options) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'create', [options]);
        });
    }
    join(conferenceId, options = {}) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'join', [conferenceId, options]);
        });
    }
    broadcast(conferenceId) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'broadcast', [conferenceId]);
        });
    }
    leave() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'leave', []);
        });
    }
    invite(conferenceId, participants) {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'invite', [conferenceId, array]);
        });
    }
    sendBroadcastMessage(message) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'sendBroadcastMessage', [message]);
            resolve();
        });
    }
    setAudio3DEnabled(enabled) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setAudio3DEnabled', [enabled]);
        });
    }
    isAudio3DEnabled() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'isAudio3DEnabled', []));
    }
    setUIConfiguration(configuration) {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'setUIConfiguration', [configuration]));
    }
    setTelecomMode(enabled) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setTelecomMode', [enabled]);
        });
    }
    isTelecomMode() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'isTelecomMode', []));
    }
    appearMaximized(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'appearMaximized', [enabled]);
            resolve();
        });
    }
    defaultBuiltInSpeaker(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'defaultBuiltInSpeaker', [enabled]);
            resolve();
        });
    }
    defaultVideo(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'defaultVideo', [enabled]);
            resolve();
        });
    }
    startRecording() {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'startRecording', []);
            resolve();
        });
    }
    stopRecording() {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'stopRecording', []);
            resolve();
        });
    }
    /*
     *  Android methods
     */
    screenAutoLock(enabled) {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'screenAutoLock', [enabled]);
            resolve();
        });
    }
    isUserLoggedIn() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'isUserLoggedIn', []));
    }
    checkForAwaitingConference() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'checkForAwaitingConference', []));
    }
    /*
     *  Deprecated methods
     */
    startConference(conferenceId, participants) {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'startConference', [conferenceId, array]);
        });
    }
    stopConference() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'stopConference', []));
    }
    openSession(userInfo) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'connect', [userInfo.json()]);
        });
    }
    closeSession() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'disconnect', []));
    }
    //method to refresh tokens, used internally
    onAccessTokenOk(accessToken) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'onAccessTokenOk', [accessToken]);
        });
    }
    onAccessTokenKo(errorMessage) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'onAccessTokenKo', [errorMessage]);
        });
    }
}
exports.Voxeet = new Voxeet_();
//export default new Voxeet(); // will be available through Voxeet not voxeet -> fake 'singleton'
//# sourceMappingURL=VoxeetSDK.js.map