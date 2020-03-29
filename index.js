import { NativeModules } from 'react-native';
import {AppRegistry} from 'react-native';

const { BackgroundGeofencing } = NativeModules;

export const onGeofenceEvent = onGeofenceEventCallback => {
  if (typeof onGeofenceEventCallback === "function") {
    AppRegistry.registerHeadlessTask('OnGeoFenceEventJavaScript', () => onGeofenceEventCallback);
  }
}

export default {
  add(geofence = {}) {
    const { id, lat, lng } = geofence;
    const defaults = {
      radius: 150,
      expiration: -1,
      notificationResponsiveness: 0,
      loiteringDelay: 0,
      setDwellTransitionType: false,
      initialiseOnDeviceRestart: false,
      setInitialTriggers: true,
    };
    if (!id || !lat || !lng) {
      throw new Error("RN:BackgroundGeofencing:", "Must provide atlease a valid id, lat and lng");
    }
    return BackgroundGeofencing.add({ ...defaults, ...geofence});
  }
}
