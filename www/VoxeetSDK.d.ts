import UserInfo from "./UserInfo";
import VoxeetMedia from "./VoxeetMedia";
import { Configuration } from "./Configurations";
export declare enum RTCPMode {
    WORST = "worst",
    BEST = "best"
}
export declare enum Mode {
    STANDARD = "standard",
    PUSH = "push"
}
export declare enum Codec {
    VP8 = "VP8",
    H264 = "H264"
}
export { Configuration as Configuration } from "./Configurations";
export { Users as Users } from "./Configurations";
export { Overlay as Overlay } from "./Configurations";
export { ActionBar as ActionBar } from "./Configurations";
export interface CreateParameters {
    ttl?: number;
    rtcpMode?: RTCPMode;
    mode?: Mode;
    videoCodec?: Codec;
    liveRecording?: boolean;
}
export interface CreateOptions {
    alias?: string;
    params?: CreateParameters;
}
export declare enum UserType {
    USER = "user",
    LISTENER = "listener"
}
export interface JoinUserInfo {
    type?: UserType;
}
export interface JoinOptions {
    user?: JoinUserInfo;
}
export interface RefreshCallback {
    (): void;
}
export interface TokenRefreshCallback {
    (): Promise<string>;
}
export interface ConferenceStatusUpdated {
    state: string;
    conferenceAlias: string;
    conferenceId: string;
}
export interface ConferenceStatusUpdatedEventCallback {
    (): Promise<ConferenceStatusUpdated>;
}
declare class Voxeet_ {
    VoxeetMedia: VoxeetMedia;
    refreshAccessTokenCallback: RefreshCallback | null;
    refreshToken: TokenRefreshCallback | undefined;
    constructor();
    initialize(consumerKey: string, consumerSecret: string): Promise<any>;
    initializeToken(accessToken: string | undefined, refreshToken: TokenRefreshCallback): Promise<unknown>;
    connect(userInfo: UserInfo): Promise<any>;
    disconnect(): Promise<any>;
    create(options: CreateOptions): Promise<any>;
    join(conferenceId: string, options?: JoinOptions): Promise<any>;
    broadcast(conferenceId: string): Promise<any>;
    leave(): Promise<any>;
    invite(conferenceId: string, participants: UserInfo[]): Promise<any>;
    sendBroadcastMessage(message: string): Promise<any>;
    setAudio3DEnabled(enabled: boolean): Promise<any>;
    isAudio3DEnabled(): Promise<boolean>;
    setUIConfiguration(configuration: Configuration): Promise<unknown>;
    setTelecomMode(enabled: boolean): Promise<any>;
    isTelecomMode(): Promise<boolean>;
    appearMaximized(enabled: boolean): Promise<any>;
    defaultBuiltInSpeaker(enabled: boolean): Promise<any>;
    defaultVideo(enabled: boolean): Promise<any>;
    startRecording(): Promise<unknown>;
    stopRecording(): Promise<unknown>;
    screenAutoLock(enabled: boolean): Promise<any>;
    isUserLoggedIn(): Promise<boolean>;
    checkForAwaitingConference(): Promise<any>;
    onConferenceStatusUpdatedEvent(callback: ConferenceStatusUpdatedEventCallback): Promise<unknown>;
    /** @deprecated */
    startConference(conferenceId: string, participants: Array<UserInfo>): Promise<any>;
    /** @deprecated */
    stopConference(): Promise<any>;
    /** @deprecated use connect instead */
    openSession(userInfo: UserInfo): Promise<any>;
    /** @deprecated use disconnect instead */
    closeSession(): Promise<any>;
    onAccessTokenOk(accessToken: string): Promise<unknown>;
    onAccessTokenKo(errorMessage: string): Promise<unknown>;
}
export declare const Voxeet: Voxeet_;
