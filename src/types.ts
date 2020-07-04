export interface RNBackgroundGeofencing {
  configureWebhook(webhook: RNBackgroundGeofencingWebhook): Promise<boolean>;
  configureNotification(notification: RNBackgroundGeofencingNotification): Promise<boolean>;
  add(geofence: RNBackgroundGeofence): Promise<string>;
  isLocationPermissionGranted(): Promise<boolean>;
  isLocationServicesEnabled(): Promise<boolean>;
  // start android specific methods
  isGooglePlayServicesAvailable(): Promise<boolean>;
  requestEnableGooglePlayServices(): Promise<boolean>;
  requestEnableLocationServices(): Promise<boolean>;
  // end android specific methods
  openLocationServicesSettings(): void;
  remove(geofenceId: string): Promise<string>;
  init(): void;
  restart(): void;
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

export type ERROR_CODES = 'invalid_platform' | 'geofence_exception';

export type RNGeofenceTransitionType = 'enter' | 'exit' | 'dwell';

export class RNBackgroundGeofencingException extends Error {
  code: string;
  constructor(error: { code: ERROR_CODES; message: string }) {
    super(error.message);
    this.name = 'RNBackgroundGeofencingException';
    this.message = error.message;
    this.code = error.code;
  }
}

export interface RNBackgroundGeofencingAndroidNotification {
  importance: number;
  channelId: string;
  channelName: string;
  channelDescription: string;
}

export interface RNGeofenceTransitionData {
  accuracy: number;
  altitude: number;
  bearing: number;
  time: number;
  provider: string;
  lat: number;
  lng: number;
  geofenceIds: Array<string>;
}

export interface RNBackgroundGeofencingNotification {
  title: string;
  text: string;
  android: RNBackgroundGeofencingAndroidNotification;
}

export interface RNBackgroundGeofencingTransitionEvent {
  transition: RNGeofenceTransitionType;
  data: RNGeofenceTransitionData;
}

export interface RNBackgroundGeofencingJSTask {
  task: (transition: RNBackgroundGeofencingTransitionEvent) => Promise<any>;
  notification: RNBackgroundGeofencingNotification;
}

export interface RNBackgroundGeofencingConfiguration {
  webhook?: RNBackgroundGeofencingWebhook;
  jsTask?: RNBackgroundGeofencingJSTask;
}

export interface RNBackgroundGeofence {
  id: string;
  lat: number;
  lng: number;
  radius: number;
  expiresIn: string | number;
  loiteringDelay: string | number;
  notificationResponsiveness: string | number;
  transitionTypes: Array<RNGeofenceTransitionType>;
  initialTriggerTransitionTypes: Array<RNGeofenceTransitionType>;
  registerOnDeviceRestart: boolean;
}
