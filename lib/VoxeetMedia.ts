/** This class enable interaction with VoxeetUXKit */
const exec:any = require('cordova/exec');
const SERVICE_MEDIA:string = 'VoxeetMedia';

export class VoxeetMedia {

    constructor() {
    }

    /**
     * Starts the video.
     */
    startVideo(usingFrontCamera: boolean): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'startVideo', [usingFrontCamera]);
        });
    }

    /**
     * Stops the video.
     */
    stopVideo(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE_MEDIA, 'stopVideo', []);
        });
    }

    /**
     * Switches the camera.
     */
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