export class UserInfo {

    public name: string;
    public externalId: string;
    public avatarUrl: string | undefined;

    constructor (externalId: string, name: string, avatarUrl: string | undefined) {
        this.name = name;
        this.externalId = externalId;
        this.avatarUrl = avatarUrl;
    }

    json () {
        return {
            name: this.name,
            externalId: this.externalId,
            avatarUrl: this.avatarUrl
        };
    }
}

//@deprecated we will use a non default form in the future
export default UserInfo;