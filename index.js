import {NativeModules, Platform} from 'react-native';
import {AppRegistry, PermissionsAndroid} from 'react-native';

const {BackgroundGeofencing} = NativeModules;

const defaultWebhookConfiguration = {
  url: null,
  headers: {},
  timeout: 15000,
  exclude: [],
};

const RNGeofenceEventNameMap = {
  GEOFENCE_TRANSITION_ENTER: 'ENTER',
  GEOFENCE_TRANSITION_EXIT: 'EXIT',
  GEOFENCE_TRANSITION_DWELL: 'DWELL',
  GEOFENCE_TRANSITION_UNKNOWN: 'UNKNOWN',
  GEOFENCE_TRANSITION_ERROR: 'ERROR',
};

export const RNGeofenceEvent = {
  ENTER: 'ENTER',
  EXIT: 'EXIT',
  DWELL: 'DWELL',
  UNKNOWN: 'UNKNOWN',
  ERROR: 'ERROR',
};

export const configureWebhook = async (webhookConfig = {}) => {
  if (Platform.OS !== 'android') {
    return;
  }
  if (webhookConfig?.url) {
    await BackgroundGeofencing.configureWebhook({
      ...defaultWebhookConfiguration,
      ...webhookConfig,
    });
    return;
  } else {
    throw new Error('configureWebhook requires a valid url');
  }
};

export const configureNotification = async (notification = {}) => {
  if (Platform.OS !== 'android') {
    return;
  }
  await BackgroundGeofencing.configureNotification(notification);
};

export const isLocationPermissionGranted = () => {
  if (Platform.OS !== 'android') {
    console.warn(
      'hasLocationPermission function only works on Android platform',
    );
    return Promise.resolve(false);
  }
  return BackgroundGeofencing.hasLocationPermission();
};

export const isLocationServicesEnabled = () => {
  if (Platform.OS !== 'android') {
    console.warn(
      'isLocationServicesEnabled function only works on Android platform',
    );
    return Promise.resolve(false);
  }
  return BackgroundGeofencing.isLocationServicesEnabled();
};

export const openLocationServicesSettings = () => {
  BackgroundGeofencing.openLocationServicesSettings();
};

export const requestEnableLocationServices = () => {
  return BackgroundGeofencing.requestEnableLocationServices();
};

export const isGooglePlayServicesAvailable = () => {
  return BackgroundGeofencing.isGooglePlayServicesAvailable();
};

export const requestEnableGooglePlayServices = () => {
  return BackgroundGeofencing.requestEnableGooglePlayServices();
};

export const hasLocationPermission = () => {
  console.warn(
    '[depreication warning]: hasLocationPermission has been depricated in favor for isLocationPermissionGranted',
  );
  return isLocationPermissionGranted();
};

export const requestLocationPermission = async (rationale = {}) => {
  try {
    const hasPermission = await BackgroundGeofencing.hasLocationPermission();
    if (hasPermission) {
      return true;
    }
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      rationale,
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      return true;
    }
    return false;
  } catch (error) {
    console.warn(error);
    return false;
  }
};

export default {
  async add(geofence = {}) {
    const {id, lat, lng} = geofence;
    const defaults = {
      radius: 150,
      expiration: -1,
      notificationResponsiveness: 0,
      loiteringDelay: 0,
      setDwellTransitionType: false,
      registerOnDeviceRestart: false,
      setInitialTriggers: true,
    };
    try {
      if (!id || !lat || !lng) {
        throw new Error(
          'RN:BackgroundGeofencing: Must provide at least a valid id, lat and lng',
        );
      }
      if (Platform.OS === 'android') {
        return await BackgroundGeofencing.add({...defaults, ...geofence});
      }
      return id;
    } catch (error) {
      throw error;
    }
  },

  remove(geofenceId) {
    if (typeof geofenceId === 'string' && Platform.OS === 'android') {
      BackgroundGeofencing.remove(geofenceId);
    }
  },

  async configure(configuration = {}) {
    try {
      const {notification, webhook, jsTask} = configuration;
      const task = jsTask?.task;
      const config = {};
      if (Platform.OS !== 'android') {
        return;
      }
      if (typeof notification === 'object') {
        config['notification'] = notification;
      }
      if (typeof webhook === 'object') {
        config['webhook'] = {...defaultWebhookConfiguration, ...webhook};
      }
      if (typeof task === 'function') {
        config['jsTask'] = {hasJSTask: true};
        AppRegistry.registerHeadlessTask('OnGeoFenceEventJavaScript', () => {
          return async ({event, data}) => {
            try {
              await task({
                event: RNGeofenceEventNameMap[event],
                data: JSON.parse(data),
              });
            } catch (error) {
              console.error(`[RNBackgroundGeofencing]`, error);
            }
          };
        });
      }
      await BackgroundGeofencing.configure(config);
    } catch (error) {
      throw error;
    }
  },

  init() {
    BackgroundGeofencing.init();
  },

  restart() {
    BackgroundGeofencing.restart();
  },
};
