declare module 'user-agents' {
  class UserAgent {
    constructor(options?: any);
    toString(): string;
    data: {
      userAgent: string;
      appName: string;
      connection: any;
      platform: string;
      pluginsLength: number;
      screenHeight: number;
      screenWidth: number;
      vendor: string;
      viewportHeight: number;
      viewportWidth: number;
    };
  }
  export default UserAgent;
}

