"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * This class enable interaction with VoxeetUXKit
 * @constructor
 */
const exec = require('cordova/exec');
const SERVICE_MEDIA = 'VoxeetMedia';
class VoxeetMedia {
    constructor() {
    }
    startVideo(usingFrontCamera) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'startVideo', [usingFrontCamera]);
        });
    }
    stopVideo() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'stopVideo', []);
        });
    }
    switchCamera() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'switchCamera', []);
        });
    }
}
exports.default = VoxeetMedia;
//# sourceMappingURL=VoxeetMedia.js.map