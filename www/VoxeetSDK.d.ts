import VoxeetMedia from "./VoxeetMedia";
import ConferenceUser from "./types/ConferenceUser";
import { default as Configuration } from "./types/Configurations";
import { CreateOptions, CreateResult } from './types/CreateConference';
import { JoinOptions } from './types/JoinConference';
export interface RefreshCallback {
    (): void;
}
export interface TokenRefreshCallback {
    (): Promise<string>;
}
declare class Voxeet_ {
    VoxeetMedia: VoxeetMedia;
    refreshAccessTokenCallback: RefreshCallback | null;
    refreshToken: TokenRefreshCallback | undefined;
    constructor();
    /**
     * Initializes the SDK using the customer key and secret.
     * @param consumerKey Consumer Key
     * @param consumerSecret Consumer Secret
     */
    initialize(consumerKey: string, consumerSecret: string): Promise<string>;
    /**
     * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
     * @param accessToken Access token
     * @param refreshToken Callback to get a new access token after it expires
     */
    initializeToken(accessToken: string | undefined, refreshToken: TokenRefreshCallback): Promise<string>;
    /**
     * Opens a new session.
     * @param userInfo Participant information
     */
    connect(userInfo: ConferenceUser): Promise<string>;
    /**
     * Closes the current session.
     */
    disconnect(): Promise<string>;
    /**
     * Creates a conference.
     * @param options Options to use to create the conference
     */
    create(options: CreateOptions): Promise<CreateResult>;
    /**
     * Joins the conference.
     * @param conferenceId Id of the conference to join
     * @param options Options to use to join the conference
     */
    join(conferenceId: string, options?: JoinOptions): Promise<string>;
    /**
     * Joins the conference in the broadcaster mode which allows transmitting audio and video.
     * @param conferenceId Id of the conference to join
     */
    broadcast(conferenceId: string): Promise<string>;
    /**
     * Leaves the conference.
     */
    leave(): Promise<string>;
    /**
     * Invite a participant to the conference.
     * @param conferenceId Id of the conference to invite the participant to
     * @param participants List of participants to invite
     */
    invite(conferenceId: string, participants: Array<ConferenceUser>): Promise<any>;
    /**
     * Sends a broadcast message to the participants of the conference.
     * @param message Message to send to the other participants
     */
    sendBroadcastMessage(message: string): Promise<any>;
    /**
     * Sets if you want to enable audio 3D.
     * @param enabled True to enable audio 3D
     */
    setAudio3DEnabled(enabled: boolean): Promise<any>;
    /**
     * Is audio 3D enabled.
     */
    isAudio3DEnabled(): Promise<boolean>;
    /**
     * Sets the UI configuration.
     * @param configuration UI configuration
     */
    setUIConfiguration(configuration: Configuration): Promise<unknown>;
    /**
     * Sets if you want to enable the Telecom mode or not.
     * @param enabled True to enable the Telecom mode
     */
    setTelecomMode(enabled: boolean): Promise<any>;
    /**
     * Is telecom mode enabled.
     */
    isTelecomMode(): Promise<boolean>;
    /**
     * Sets if you want the UXKit to appear maximized or not.
     * @param maximized True to have the UXKit to appear maximized
     */
    appearMaximized(enabled: boolean): Promise<void>;
    /**
     * Use the built in speaker by default.
     * @param enable True to use the built in speaker by default
     */
    defaultBuiltInSpeaker(enabled: boolean): Promise<void>;
    /**
     * Sets the video on by default.
     * @param enable True to turn on the video by default
     */
    defaultVideo(enabled: boolean): Promise<void>;
    /**
     * Starts recording the conference.
     */
    startRecording(): Promise<unknown>;
    /**
     * Stops recording the conference.
     */
    stopRecording(): Promise<unknown>;
    /**
     * Activates or disable the screen auto lock. Android only.
     * @param activate True to activate the screen auto lock
     */
    screenAutoLock(enabled: boolean): Promise<any>;
    /**
     * @deprecated Android only.
     */
    isUserLoggedIn(): Promise<boolean>;
    /**
     * Checks if a conference is awaiting. Android only.
     */
    checkForAwaitingConference(): Promise<any>;
    /** @deprecated Use join() instead. */
    startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise<any>;
    /** @deprecated Use leave() instead. */
    stopConference(): Promise<any>;
    /** @deprecated use connect instead. */
    openSession(userInfo: ConferenceUser): Promise<any>;
    /** @deprecated use disconnect instead. */
    closeSession(): Promise<any>;
    /** Internal method used to refresh tokens. */
    onAccessTokenOk(accessToken: string): Promise<unknown>;
    /** Internal method. */
    onAccessTokenKo(errorMessage: string): Promise<unknown>;
}
export declare const Voxeet: Voxeet_;
export {};
