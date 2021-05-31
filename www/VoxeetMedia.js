"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/** This class enable interaction with VoxeetUXKit */
const exec = require('cordova/exec');
const SERVICE_MEDIA = 'VoxeetMedia';
class VoxeetMedia {
    constructor() {
    }
    /**
     * Starts the video.
     */
    startVideo(usingFrontCamera) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'startVideo', [usingFrontCamera]);
        });
    }
    /**
     * Stops the video.
     */
    stopVideo() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'stopVideo', []);
        });
    }
    /**
     * Switches the camera.
     */
    switchCamera() {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'switchCamera', []);
        });
    }
}
exports.default = VoxeetMedia;
//# sourceMappingURL=VoxeetMedia.js.map