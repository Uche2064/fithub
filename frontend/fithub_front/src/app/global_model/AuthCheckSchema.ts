export class AuthCheckSchema {
  userName!: string;
  token!: string;
  constructor(userName: string, token: string) {
    this.userName = userName;
    this.token = token;
  }
}
