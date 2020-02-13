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
const exec:any = require('cordova/exec');
const SERVICE_MEDIA:string = 'VoxeetMedia';

export default class VoxeetMedia {

    constructor() {
    }

    startVideo(usingFrontCamera: boolean): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'startVideo', [usingFrontCamera]);
        });
    }

    stopVideo(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'stopVideo', []);
        });
    }

    switchCamera(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'switchCamera', []);
        });
    }

    //maybe for future references
    /*isDefaultFrontFacing(): Promise<boolean>  {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'isDefaultFrontFacing', []);
        });
    }*/

    //maybe for future references
    /*isFrontCamera(): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'isFrontCamera', []);
        });
    }*/
}