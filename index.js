import {NativeModules} from 'react-native';
import {AppRegistry} from 'react-native';

const {BackgroundGeofencing} = NativeModules;

const defaultWebhookConfiguration = {
  url: null,
  headers: null,
  timeout: 15000,
  exclude: [],
};

export const configure = (configuration = {}) => {
  const {jsTask} = configuration;
  let {webhook} = configuration;
  if (jsTask && typeof jsTask !== 'function') {
    throw new Error('invalid jsTask function provided');
  }
  if (webhook && typeof webhook !== 'object') {
    throw new Error('invalid webhook configuration provided');
  }
  if (webhook && typeof webhook.url !== 'string') {
    throw new Error('invalid webhook configuration provided');
  }
  if (jsTask) {
    AppRegistry.registerHeadlessTask('OnGeoFenceEventJavaScript', () => jsTask);
  }
  if (webhook) {
    BackgroundGeofencing.configureWebhook({
      ...defaultWebhookConfiguration,
      ...webhook,
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
};
