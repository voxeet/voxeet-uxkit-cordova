export declare enum ConferenceStatus {
    DEFAULT = "DEFAULT",
    CREATING = "CREATING",
    CREATED = "CREATED",
    JOINING = "JOINING",
    JOINED = "JOINED",
    LEAVING = "LEAVING",
    LEFT = "LEFT",
    ERROR = "ERROR",
    DESTROYED = "DESTROYED",
    ENDED = "ENDED"
}
export interface ConferenceStatusUpdated {
    status: ConferenceStatus;
    conferenceAlias: string;
    conferenceId: string;
}
export declare function fromRawToConferenceStatus(raw: any): ConferenceStatusUpdated | null;
