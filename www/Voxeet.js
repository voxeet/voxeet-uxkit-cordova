/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * 'License'); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

    openSession (userId, participantName, avatarUrl) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'openSession', [userId, participantName, avatarUrl]);
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
        exec(null, null, SERVICE, 'add', [null]);
    }

    update (/* participant */) {
        exec(null, null, SERVICE, 'update', [null]);
    }

    remove (/* participant */) {
        exec(null, null, SERVICE, 'remove', [null]);
    }

    appearMaximized (enabled) {
        exec(null, null, SERVICE, 'appearMaximized', [enabled]);
    }

    defaultBuiltInSpeaker (enabled) {
        exec(null, null, SERVICE, 'defaultBuiltInSpeaker', [enabled]);
    }

    screenAutoLock (enabled) {
        exec(null, null, SERVICE, 'screenAutoLock', [enabled]);
    }

}

module.exports = new Voxeet(); // will be available through Voxeet not voxeet -> fake 'singleton'
