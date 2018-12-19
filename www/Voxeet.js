/*
 *
 * Voxeet ConferenceKit Cordova
 * Copyright (C) 2018
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
 * This class enable interaction with Voxeet ConferenceKit
 * @constructor
 */
const exec = require('cordova/exec');
const SERVICE = 'Voxeet';

class Voxeet {

    constructor() {
        this.refreshAccessTokenCallback = () => {
            this.refreshToken()
            .then(accessToken => this.onAccessTokenOk(accessToken))
            .catch(err => {
                console.log(err);
                this.onAccessTokenKo("Error while refreshing token");
            });
        }
    }

    initialize(consumerKey, consumerSecret) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'initialize', [consumerKey, consumerSecret]);
        });
    }

    initializeWithRefresh(accessToken, refreshToken) {
        return new Promise((resolve, reject) => {
            this.refreshToken = refreshToken;
            exec(this.refreshAccessTokenCallback, (err) => {}, SERVICE, 'refreshAccessTokenCallback', []);
            exec(resolve, reject, SERVICE, 'initializeWithRefresh', [accessToken]);
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

    create(parameters) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'create', [parameters]);
        });
    }

    join(conferenceId) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'join', [conferenceId]);
        });
    }

    leave() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'leave', [conferenceId]);
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
            exec(resolve, reject, SERVICE, 'openSession', [userInfo.json()]);
        });
    }

    closeSession() {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'closeSession', []));
    }

    //method to refresh tokens, used internally
    onAccessTokenOk (accessToken) {
      return new Promise((resolve, reject) => {
        exec(resolve, reject, SERVICE, 'onAccessTokenOk', [accessToken]);
      });
    }

    onAccessTokenKo (errorMessage) {
      return new Promise((resolve, reject) => {
        exec(resolve, reject, SERVICE, 'onAccessTokenKo', [errorMessage]);
      });
    }
}

module.exports = new Voxeet(); // will be available through Voxeet not voxeet -> fake 'singleton'
