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

    initialize (consumerKey, consumerSecret) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'initialize', [consumerKey, consumerSecret]);
        });
    }

    openSession (userInfo) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'openSession', [userInfo.json()]);
        });
    }

    closeSession () {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'closeSession', []));
    }

    startConference (conferenceId, participants) {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'startConference', [conferenceId, array]);
        });
    }

    stopConference () {
        return new Promise((resolve, reject) => exec(resolve, reject, SERVICE, 'stopConference', []));
    }

    add (/* participant */) {
        return new Promise((resolve, reject) => {
          exec(null, null, SERVICE, 'add', [null]);
          resolve();
        });
    }

    update (/* participant */) {
        return new Promise((resolve, reject) => {
          exec(null, null, SERVICE, 'update', [null]);
          resolve();
        });
    }

    remove (/* participant */) {
        return new Promise((resolve, reject) => {
          exec(null, null, SERVICE, 'remove', [null]);
          resolve();
        });
    }

    appearMaximized (enabled) {
        return new Promise((resolve, reject) => {
          exec(null, null, SERVICE, 'appearMaximized', [enabled]);
          resolve();
        });
    }

    defaultBuiltInSpeaker (enabled) {
        return new Promise((resolve, reject) => {
          exec(null, null, SERVICE, 'defaultBuiltInSpeaker', [enabled]);
          resolve();
        });
    }

    screenAutoLock (enabled) {
        return new Promise((resolve, reject) => {
          exec(null, null, SERVICE, 'screenAutoLock', [enabled]);
          resolve();
        });
    }

    sendBroadcastMessage (message) {
      return new Promise((resolve, reject) => {
        exec(null, null, SERVICE, 'sendBroadcastMessage', [message]);
        resolve();
      });
    }

}

module.exports = new Voxeet(); // will be available through Voxeet not voxeet -> fake 'singleton'
