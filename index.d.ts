// Type definitions for react-native-background-geofencing
// Project: https://github.com/jgkiano/react-native-background-geofencing
// Definitions by: Kiano (@jgkiano)
// Definitions: https://github.com/jgkiano/react-native-background-geofencing/blob/master/index.d.ts

interface GeofenceData {
  event:
    | 'GEOFENCE_TRANSITION_ENTER'
    | 'GEOFENCE_TRANSITION_EXIT'
    | 'GEOFENCE_TRANSITION_DWELL'
    | 'GEOFENCE_TRANSITION_UNKNOWN'
    | 'GEOFENCE_TRANSITION_ERROR';
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

interface JSTaskConfig {
  task: (data: GeofenceData) => Promise<any>;
  notification: {
    title: string;
    text: string;
  };
}

interface WebhookConfig {
  url: string;
  headers?: Array<{[key: string]: any}>;
  timeout?: number;
  exclude?: Array<string>;
}

interface Geofence {
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

export interface GeofenceEvent {
  ENTER: string;
  EXIT: string;
  DWELL: string;
  UNKNOWN: string;
  ERROR: string;
}

export function configureJSTask(jsTakConfig: JSTaskConfig): void;

export function configureWebhook(webhookConfig: WebhookConfig): void;

export interface RNBackgroundGeofencing {
  add(geofence: Geofence): Promise<string>;
  remove(geofenceId: string): void;
}

declare const BackgroundGeofencing: RNBackgroundGeofencing;

export default BackgroundGeofencing;
