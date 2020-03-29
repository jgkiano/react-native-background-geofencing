import {NativeModules} from 'react-native';
import {AppRegistry} from 'react-native';

const {BackgroundGeofencing} = NativeModules;

export const onGeofenceEvent = onGeofenceEventCallback => {
  if (typeof onGeofenceEventCallback === 'function') {
    AppRegistry.registerHeadlessTask(
      'OnGeoFenceEventJavaScript',
      () => onGeofenceEventCallback,
    );
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
      initialiseOnDeviceRestart: false,
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
