import {NativeModules, Platform} from 'react-native';
import {AppRegistry} from 'react-native';

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

export const configureJSTask = (jsTask = {}) => {
  if (Platform.OS !== 'android') {
    return;
  }

  if (typeof jsTakConfig !== 'object') {
    throw new Error('invalid JavaScript task configuration provided');
  }

  const task = jsTask?.task;

  if (typeof task === 'function') {
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

export const hasLocationPermission = () => {
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

  async init() {
    try {
      const hasLocationPermission = await hasLocationPermission();
      const isLocationServicesEnabled = await isLocationServicesEnabled();
      if (
        hasLocationPermission &&
        isLocationServicesEnabled &&
        Platform.OS === 'android'
      ) {
        BackgroundGeofencing.reRegisterErroneousGeofences();
      }
    } catch (error) {
      throw error;
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
};
