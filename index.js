import { NativeModules } from 'react-native';
import {AppRegistry} from 'react-native';

const { BackgroundGeofencing } = NativeModules;

export const onGeofenceEvent = onGeofenceEventCallback => {
  if (typeof onGeofenceEventCallback === "function") {
    AppRegistry.registerHeadlessTask('OnGeoFenceEventJavaScript', () => onGeofenceEventCallback);
  }
}

export default BackgroundGeofencing;
