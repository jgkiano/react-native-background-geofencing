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

export interface RNTransitionEvent {
  RN_GEOFENCE_TRANSITION_DATA: string;
}

export interface RNTransitionEventData {
  ids: Array<string>;
  transition_date: number;
  geopoint_provider: string;
  geo_point: {
    lat: number;
    lon: number;
  };
  gps_accuracy: number;
  transition_event: RNGeofenceTransitionType;
  geo_point_source: 'geofence';
  device_os_name: 'android' | 'ios';
  device_os_version: string;
  device_manufacturer: string;
  device_model: string;
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
  importance?: number;
  channelId?: string;
  channelName?: string;
  channelDescription?: string;
}

export interface RNBackgroundGeofencingTransitionEvent {
  transition: RNGeofenceTransitionType;
  data: RNGeofenceTransitionData;
}

export interface RNBackgroundGeofencingJSTask {
  task: (transition: RNTransitionEventData) => any;
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
