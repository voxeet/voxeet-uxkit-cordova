export default class VoxeetMedia {
    constructor();
    /**
     * Starts the video.
     */
    startVideo(usingFrontCamera: boolean): Promise<boolean>;
    /**
     * Stops the video.
     */
    stopVideo(): Promise<boolean>;
    /**
     * Switches the camera.
     */
    switchCamera(): Promise<boolean>;
}
