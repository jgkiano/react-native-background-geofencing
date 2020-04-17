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

export const configureJSTask = (jsTakConfig = {}) => {
  if (Platform.OS !== 'android') {
    return;
  }

  if (typeof jsTakConfig !== 'object') {
    throw new Error('invalid JavaScript task configuration provided');
  }

  const notification = jsTakConfig.notification || null;

  const task = jsTakConfig.task || null;

  if (typeof notification === 'object' && typeof task === 'function') {
    const {title, text} = notification;
    if (typeof title !== 'string' || typeof text !== 'string') {
      throw new Error('invalid notification configuration provided');
    } else {
      BackgroundGeofencing.configureNotification(notification);
    }
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
  if (
    Platform.OS === 'android' &&
    typeof webhookConfig === 'object' &&
    typeof webhookConfig.url === 'string'
  ) {
    return await BackgroundGeofencing.configureWebhook({
      ...defaultWebhookConfiguration,
      ...webhookConfig,
    });
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
};
