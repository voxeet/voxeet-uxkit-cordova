export enum ConferenceStatus {
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

function toConferenceStatus(str?: string): ConferenceStatus|null {
    switch(str) {
        case "ENDED": return ConferenceStatus.ENDED;
        case "DESTROYED": return ConferenceStatus.DESTROYED;
        case "ERROR": return ConferenceStatus.ERROR;
        case "LEFT": return ConferenceStatus.LEFT;
        case "LEAVING": return ConferenceStatus.LEAVING;
        case "JOINED": return ConferenceStatus.JOINED;
        case "JOINING": return ConferenceStatus.JOINING;
        case "CREATED": return ConferenceStatus.CREATED;
        case "CREATING": return ConferenceStatus.CREATING;
        case "DEFAULT": return ConferenceStatus.DEFAULT;
        default: return null;
    }
    //Android "deprecated" specific events are discarded
}

export function fromRawToConferenceStatus(raw: any): ConferenceStatusUpdated|null {
    const status = toConferenceStatus(raw.state);
    if(!status) return null;
    return {
        status,
        conferenceAlias: <string>raw.conferenceAlias,
        conferenceId: <string>raw.conferenceId
    };
} 