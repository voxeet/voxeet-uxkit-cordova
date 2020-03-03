/**
 * Describes UserInfo
 */
declare class UserInfo {
    name: string;
    externalId: string;
    avatarUrl: string | undefined;
    constructor(externalId: string, name: string, avatarUrl: string | undefined);
    json(): {
        name: string;
        externalId: string;
        avatarUrl: string | undefined;
    };
}
export = UserInfo;
