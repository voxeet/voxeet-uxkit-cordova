"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class ConferenceUser {
    constructor(externalId, name, avatarUrl) {
        this.name = name;
        this.externalId = externalId;
        this.avatarUrl = avatarUrl;
    }
    json() {
        return {
            name: this.name,
            externalId: this.externalId,
            avatarUrl: this.avatarUrl
        };
    }
}
exports.default = ConferenceUser;
//# sourceMappingURL=ConferenceUser.js.map