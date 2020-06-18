// Type definitions for react-native-background-geofencing
// Project: https://github.com/jgkiano/react-native-background-geofencing
// Definitions by: Kiano (@jgkiano)
// Definitions: https://github.com/jgkiano/react-native-background-geofencing/blob/master/index.d.ts

export interface RNGeofence {
  id: string;
  lat: number;
  lng: number;
  radius: number;
  expiration: number;
  notificationResponsiveness: number;
  loiteringDelay: number;
  setDwellTransitionType: boolean;
  registerOnDeviceRestart: boolean;
  setInitialTriggers: boolean;
}

export enum RNGeofenceEventName {
  ENTER = 'ENTER',
  EXIT = 'EXIT',
  DWELL = 'DWELL',
  UNKNOWN = 'UNKNOWN',
  ERROR = 'ERROR',
}

export interface RNGeofenceNotification {
  title?: string;
  text?: string;
  importance?: number;
  channelId?: string;
  channelName?: string;
  channelDescription?: string;
}

export interface RNGeofenceWebhook {
  url: string;
  timeout?: number;
  exclude?: Array<string>;
  headers?: {
    [key: string]: any;
  };
  meta?: {
    [key: string]: any;
  };
}

export interface RNGeofenceEventData {
  accuracy: number;
  altitude: number;
  bearing: number;
  time: number;
  provider: string;
  lat: number;
  lng: number;
  geofenceIds: Array<string>;
}

export interface RNEventPayload {
  event: RNGeofenceEventName;
  data: RNGeofenceEventData;
}

export interface RNGeofenceJsTask {
  task: (payload: RNEventPayload) => Promise<any>;
}

export interface RNGeofenceConfiguration {
  notification?: RNGeofenceNotification;
  webhook?: RNGeofenceWebhook;
  jsTask?: RNGeofenceJsTask;
}

//-- methods --//

export function isLocationServicesEnabled(): Promise<boolean>;

export function hasLocationPermission(): Promise<boolean>;

export function configureNotification(
  notification: RNGeofenceNotification,
): Promise<void>;

export function configureWebhook(webhook: RNGeofenceWebhook): Promise<void>;

export function configureJSTask(jsTask: RNGeofenceJsTask): void;

//-- default export --//

export interface BackgroundGeofencing {
  add(geofence: RNGeofence): Promise<string>;
  remove(geofenceId: string): Promise<void>;
  configure(configuration: RNGeofenceConfiguration): Promise<void>;
}

declare const RNBackgroundGeofencing: BackgroundGeofencing;

export default RNBackgroundGeofencing;
