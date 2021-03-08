"use strict";
class UserInfo {
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
module.exports = UserInfo;
//# sourceMappingURL=UserInfo.js.map