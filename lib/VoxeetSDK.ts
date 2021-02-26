export { VoxeetMedia }  from "./VoxeetMedia";
export { CreateOptions, CreateResult } from './types/CreateConference';
export { JoinOptions, JoinUserInfo, UserType } from './types/JoinConference';
export { UserInfo, Configuration} from "./types";

import { VoxeetMedia } from "./VoxeetMedia";
import { CreateOptions, CreateResult } from './types/CreateConference';
import { JoinOptions, JoinUserInfo, UserType } from './types/JoinConference';
import { UserInfo, Configuration} from "./types";

/**
 * This class enable interaction with VoxeetUXKit
 */
const exec: any = require('cordova/exec');
const SERVICE: string = 'Voxeet';

export interface RefreshCallback {
    (): void;
}

export interface TokenRefreshCallback {
    (): Promise<string>
}

export interface ConferenceStatusUpdated {
    state: string;
    conferenceAlias: string;
    conferenceId: string;
}

export interface ConferenceStatusUpdatedEventCallback {
    (): Promise<ConferenceStatusUpdated>
}

class Voxeet_ {

    public VoxeetMedia: VoxeetMedia;

    refreshAccessTokenCallback: RefreshCallback | null = null;
    refreshToken: TokenRefreshCallback | undefined; 

    constructor() {
        this.VoxeetMedia = new VoxeetMedia();
        
        this.refreshAccessTokenCallback = () => {
            this.refreshToken && this.refreshToken()
            .then(accessToken => this.onAccessTokenOk(accessToken))
            .catch(err => {
                console.log(err);
                this.onAccessTokenKo("Error while refreshing token");
           });
        }
    }

    /**
     * Initializes the SDK using the customer key and secret.
     * @param consumerKey Consumer Key
     * @param consumerSecret Consumer Secret
     */
    initialize(consumerKey: string, consumerSecret: string): Promise<string> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'initialize', [consumerKey, consumerSecret]);
        });
    }

    /**
     * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
     * @param accessToken Access token
     * @param refreshToken Callback to get a new access token after it expires
     */
    initializeToken(accessToken: string | undefined, refreshToken: TokenRefreshCallback): Promise<string> {
        return new Promise((resolve, reject) => {
            this.refreshToken = refreshToken;
            exec(this.refreshAccessTokenCallback, (err: Error) => {}, SERVICE, 'refreshAccessTokenCallback', []);
            exec(resolve, reject, SERVICE, 'initializeToken', [accessToken]);
        });
    }

    /**
     * Opens a new session.
     * @param userInfo Participant information
     */
    connect(userInfo: UserInfo): Promise<string> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'connect', [userInfo.json()]);
        });
    }

    /**
     * Closes the current session.
     */
    disconnect(): Promise<string>  {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'disconnect', []);
        });
    }

    /**
     * Creates a conference.
     * @param options Options to use to create the conference
     */
    create(options: CreateOptions): Promise<CreateResult> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'create', [options]);
        });
    }

    /**
     * Joins the conference.
     * @param conferenceId Id of the conference to join
     * @param options Options to use to join the conference
     */
    join(conferenceId: string, options: JoinOptions = {}): Promise<string>  {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'join', [conferenceId, options]);
        });
    }

    /**
     * Joins the conference in the broadcaster mode which allows transmitting audio and video.
     * @param conferenceId Id of the conference to join
     */
    broadcast(conferenceId: string): Promise<string>  {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'broadcast', [conferenceId]);
        });
    }

    /**
     * Leaves the conference.
     */
    leave(): Promise<string>  {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'leave', []);
        });
    }

    /**
     * Invite a participant to the conference.
     * @param conferenceId Id of the conference to invite the participant to
     * @param participants List of participants to invite
     */
    invite(conferenceId: string, participants: Array<UserInfo>): Promise<string> {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'invite', [conferenceId, array]);
        });
    }

    /**
     * Sends a broadcast message to the participants of the conference.
     * @param message Message to send to the other participants
     */
    sendBroadcastMessage(message: string): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'sendBroadcastMessage', [message]);
            resolve();
        });
    }

    /**
     * Sets if you want to enable audio 3D.
     * @param enabled True to enable audio 3D
     */
    setAudio3DEnabled(enabled: boolean): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setAudio3DEnabled', [enabled]);
        });
    }

    /**
     * Is audio 3D enabled.
     */
    isAudio3DEnabled(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'isAudio3DEnabled', []);
        });
    }

    /**
     * Sets the UI configuration.
     * @param configuration UI configuration
     */
    setUIConfiguration(configuration: Configuration) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setUIConfiguration', [configuration]);
        });
    }

    /**
     * Sets if you want to enable the Telecom mode or not.
     * @param enabled True to enable the Telecom mode
     */
    setTelecomMode(enabled: boolean): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'setTelecomMode', [enabled]);
        });
    }

    /**
     * Is telecom mode enabled.
     */
    isTelecomMode(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'isTelecomMode', []);
        });
    }

    /**
     * Sets if you want the UXKit to appear maximized or not.
     * @param maximized True to have the UXKit to appear maximized
     */
    appearMaximized(enabled: boolean): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'appearMaximized', [enabled]);
            resolve();
        });
    }

    /**
     * Use the built in speaker by default.
     * @param enable True to use the built in speaker by default
     */
    defaultBuiltInSpeaker(enabled: boolean): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'defaultBuiltInSpeaker', [enabled]);
            resolve();
        });
    }

    /**
     * Sets the video on by default.
     * @param enable True to turn on the video by default
     */
    defaultVideo(enabled: boolean): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'defaultVideo', [enabled]);
            resolve();
        });
    }

    /**
     * Starts recording the conference.
     */
    startRecording(): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'startRecording', []);
            resolve();
        });
    }

    /**
     * Stops recording the conference.
     */
    stopRecording(): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'stopRecording', []);
            resolve();
        });
    }

    /**
     * Minimize the overlay.
     */
    minimize(): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'minimize', []);
        });
    }

    /**
     * Maximize the overlay.
     */
    maximize(): Promise<void> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'maximize', []);
        });
    }

    /**
     * Activates or disable the screen auto lock. Android only.
     * @param activate True to activate the screen auto lock
     */
    screenAutoLock(enabled: boolean): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(null, null, SERVICE, 'screenAutoLock', [enabled]);
            resolve(null);
        });
    }

    /**
     * @deprecated Android only.
     */
    isUserLoggedIn(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'isUserLoggedIn', []);
        });
    }

    /**
     * Checks if a conference is awaiting. Android only.
     */
    checkForAwaitingConference(): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'checkForAwaitingConference', []);
        });
    }

    /**
     * Get notified when the conference status changes.
     * @param callback function to call when the conference status changes.
     */
    onConferenceStatusUpdatedEvent(callback: ConferenceStatusUpdatedEventCallback) {
        return new Promise((resolve, reject) => {
            exec(callback, (err: Error) => {}, SERVICE, 'onConferenceStatusUpdatedEvent', []);
        });
    }

    /** @deprecated Use join() instead. */
    startConference(conferenceId: string, participants: Array<UserInfo>): Promise<any> {
        const array = participants ? participants.map(e => e.json()) : null;
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'startConference', [conferenceId, array]);
        });
    }

    /** @deprecated Use leave() instead. */
    stopConference(): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'stopConference', []);
        });
    }

    /** @deprecated use connect instead. */
    openSession(userInfo: UserInfo): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'openSession', [userInfo.json()]);
        });
    }

    /** @deprecated use disconnect instead. */
    closeSession(): Promise<any> {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'closeSession', []);
        });
    }

    /** Internal method used to refresh tokens. */
    onAccessTokenOk(accessToken: string) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'onAccessTokenOk', [accessToken]);
        });
    }

    /** Internal method. */
    onAccessTokenKo(errorMessage: string) {
        return new Promise((resolve, reject) => {
            exec(resolve, reject, SERVICE, 'onAccessTokenKo', [errorMessage]);
        });
    }
}

export const Voxeet = new Voxeet_();

export default Voxeet;