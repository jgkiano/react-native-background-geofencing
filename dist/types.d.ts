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
declare type ERROR_CODES = 'invalid_platform' | 'geofence_exception';
export declare class RNBackgroundGeofencingException extends Error {
    code: string;
    constructor(error: {
        code: ERROR_CODES;
        message: string;
    });
}
export {};
