// Type definitions for react-native-background-geofencing
// Project: https://github.com/jgkiano/react-native-background-geofencing
// Definitions by: Kiano (@jgkiano)
// Definitions: https://github.com/jgkiano/react-native-background-geofencing/blob/master/index.d.ts

export type RNBackgroundGeofenceEventName =
  | 'ENTER'
  | 'EXIT'
  | 'DWELL'
  | 'UNKNOWN'
  | 'ERROR';

export enum RNGeofenceEvent {
  ENTER = 'ENTER',
  EXIT = 'EXIT',
  DWELL = 'DWELL',
  UNKNOWN = 'UNKNOWN',
  ERROR = 'ERROR',
}

export interface RNBackgroundGeofenceEvent {
  event: RNBackgroundGeofenceEventName;
  data: {
    accuracy?: number;
    altitude?: number;
    bearing?: number;
    time?: number;
    provider?: number;
    lat?: number;
    lng?: number;
    geofenceIds: Array<string>;
    errorMessage?: string;
  };
}

export type RNGeofenceJSTask = (
  geofenceEvent: RNBackgroundGeofenceEvent,
) => any;

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
