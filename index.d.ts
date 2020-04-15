// Type definitions for react-native-background-geofencing
// Project: https://github.com/jgkiano/react-native-background-geofencing
// Definitions by: Kiano (@jgkiano)
// Definitions: https://github.com/jgkiano/react-native-background-geofencing/blob/master/index.d.ts

export type RNGeofenceEvent =
  | 'GEOFENCE_TRANSITION_ENTER'
  | 'GEOFENCE_TRANSITION_EXIT'
  | 'GEOFENCE_TRANSITION_DWELL'
  | 'GEOFENCE_TRANSITION_UNKNOWN'
  | 'GEOFENCE_TRANSITION_ERROR';

export interface RNGeofenceEventData {
  event: RNGeofenceEvent;
  data: {
    accuracy?: number;
    altitude?: number;
    bearing?: number;
    time?: number;
    provider?: number;
    lat?: number;
    lng?: number;
    geofenceIds?: Array<string>;
  };
}

export type RNGeofenceJSTask = (
  geofenceEvent: RNGeofenceEventData,
) => Promise<any>;

export interface RNGeofenceJSTaskConfig {
  task: RNGeofenceJSTask;
  notification: {
    title: string;
    text: string;
  };
}

export interface RNGeofenceWebhookConfig {
  url: string;
  headers?: {
    [key: string]: any;
  };
  timeout?: number;
  exclude?: Array<string>;
}

export interface RNGeofence {
  id: string;
  lat: number;
  lng: number;
  radius?: number;
  expiration?: number;
  notificationResponsiveness?: number;
  loiteringDelay?: number;
  setDwellTransitionType?: boolean;
  registerOnDeviceRestart?: boolean;
  setInitialTriggers?: boolean;
}

export interface BackgroundGeofencing {
  add(geofence: RNGeofence): Promise<string>;
  remove(geofenceId: string): void;
}

export function configureJSTask(jsTakConfig: RNGeofenceJSTaskConfig): void;

export function configureWebhook(
  webhookConfig: RNGeofenceWebhookConfig,
): Promise<boolean | void>;

declare const RNBackgroundGeofencing: BackgroundGeofencing;

export default RNBackgroundGeofencing;
