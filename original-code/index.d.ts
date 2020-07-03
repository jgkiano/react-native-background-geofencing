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

export enum RNGeofenceTransitionTypes {
  enter = 'enter',
  exit = 'exit',
  dwell = 'dwell',
}

interface RNBackgroundGeofenceEventBaseData {
  geofenceIds: Array<string>;
}

export interface RNBackgroundGeofenceEventData
  extends RNBackgroundGeofenceEventBaseData {
  accuracy?: number;
  altitude?: number;
  bearing?: number;
  time?: number;
  provider?: number;
  lat?: number;
  lng?: number;
}

export interface RNBackgroundGeofenceEventErrorData
  extends RNBackgroundGeofenceEventBaseData {
  errorMessage?: string;
}

export interface RNBackgroundGeofenceEvent {
  event: RNBackgroundGeofenceEventName;
  data: RNBackgroundGeofenceEventData | RNBackgroundGeofenceEventErrorData;
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
  meta?: {
    [key: string]: any;
  };
}

export interface RNGeofence {
  id: string;
  lat: number;
  lng: number;
  radius?: number;
  expiration?: number;
  notificationResponsiveness?: number;
  loiteringDelay?: number;
  registerOnDeviceRestart?: boolean;
  transitionTypes?: Array<'enter' | 'exit' | 'dwell'>;
  initialTriggerTransitionTypes?: Array<'enter' | 'exit' | 'dwell'>;
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
