import { RNBackgroundGeofencingWebhook } from './types';
/**
 * Configures a webhook that the device will use to send Geofence events via POST request
 * @param {RNBackgroundGeofencingWebhook}
 */
export declare function configureWebhook(webhook: RNBackgroundGeofencingWebhook): Promise<boolean>;
