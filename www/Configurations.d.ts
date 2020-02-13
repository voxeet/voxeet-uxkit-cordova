export interface ActionBar {
    displayMute?: boolean;
    displaySpeaker?: boolean;
    displayCamera?: boolean;
    displayScreenShare?: boolean;
    displayLeave?: boolean;
}
export interface Overlay {
    backgroundMaximizedColor?: number;
    backgroundMinimizedColor?: number;
}
export interface Users {
    speakingUserColor?: number;
    selectedUserColor?: number;
}
export interface Configuration {
    actionBar?: ActionBar;
    overlay?: Overlay;
    users?: Users;
}
