import {NativeModules} from 'react-native';
import {AppRegistry} from 'react-native';

const {BackgroundGeofencing} = NativeModules;

const defaultWebhookConfiguration = {
  url: null,
  headers: {},
  timeout: 15000,
  exclude: [],
};

export const configureJSTask = (jsTakConfig = {}) => {
  if (typeof jsTakConfig !== 'object') {
    throw new Error('invalid JavaScript task configuration provided');
  }

  const notification = jsTakConfig.notification || null;

  const task = jsTakConfig.task || null;

  if (typeof notification === 'object') {
    const {title, text} = notification;
    if (typeof title !== 'string' || typeof text !== 'string') {
      throw new Error('invalid notification configuration provided');
    }
    BackgroundGeofencing.configureNotification(notification);
  }

  if (typeof task === 'function') {
    AppRegistry.registerHeadlessTask('OnGeoFenceEventJavaScript', () => task);
  }
};

export const configureWebhook = (webhookConfig = {}) => {
  if (
    typeof webhookConfig === 'object' &&
    typeof webhookConfig.url === 'string'
  ) {
    BackgroundGeofencing.configureWebhook({
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
      return await BackgroundGeofencing.add({...defaults, ...geofence});
    } catch (error) {
      throw error;
    }
  },

  remove(geofenceId) {
    if (typeof geofenceId === 'string') {
      BackgroundGeofencing.remove(geofenceId);
    }
  },
};
