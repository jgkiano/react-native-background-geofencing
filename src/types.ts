export interface RNBackgroundGeofencing {
  configureWebhook(webhook: RNBackgroundGeofencingWebhook): Promise<boolean>;
}

export interface RNBackgroundGeofencingWebhook {
  url: string;
  timeout?: number;
  headers?: {
    [key: string]: any;
  };
  meta?: {
    [key: string]: any;
  };
}

type ERROR_CODES = 'invalid_platform' | 'geofence_exception';

export class RNBackgroundGeofencingException extends Error {
  code: string;
  constructor(error: { code: ERROR_CODES; message: string }) {
    super(error.message);
    this.name = 'RNBackgroundGeofencingException';
    this.message = error.message;
    this.code = error.code;
  }
}
