export default class VoxeetMedia {
    constructor();
    startVideo(usingFrontCamera: boolean): Promise<boolean>;
    stopVideo(): Promise<boolean>;
    switchCamera(): Promise<boolean>;
}
