"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.fromRawToConferenceStatus = exports.ConferenceStatus = void 0;
var ConferenceStatus;
(function (ConferenceStatus) {
    ConferenceStatus["DEFAULT"] = "DEFAULT";
    ConferenceStatus["CREATING"] = "CREATING";
    ConferenceStatus["CREATED"] = "CREATED";
    ConferenceStatus["JOINING"] = "JOINING";
    ConferenceStatus["JOINED"] = "JOINED";
    ConferenceStatus["LEAVING"] = "LEAVING";
    ConferenceStatus["LEFT"] = "LEFT";
    ConferenceStatus["ERROR"] = "ERROR";
    ConferenceStatus["DESTROYED"] = "DESTROYED";
    ConferenceStatus["ENDED"] = "ENDED";
})(ConferenceStatus = exports.ConferenceStatus || (exports.ConferenceStatus = {}));
function toConferenceStatus(str) {
    switch (str) {
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
function fromRawToConferenceStatus(raw) {
    const status = toConferenceStatus(raw.status);
    if (!status)
        return null;
    return {
        status,
        conferenceAlias: raw.conferenceAlias,
        conferenceId: raw.conferenceId
    };
}
exports.fromRawToConferenceStatus = fromRawToConferenceStatus;
//# sourceMappingURL=ConferenceStatus.js.map