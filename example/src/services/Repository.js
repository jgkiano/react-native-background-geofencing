import AsyncStorage from '@react-native-community/async-storage';
import BackgroundGeofencing from 'react-native-background-geofencing';

export default class Repository {
  constructor() {
    this.DB_STORED_GEOFENCE_KEY = '@storedGeofences';
    this.DB_STORED_GEOFENCE_EVENT_PREFIX = '@storedGeofenceEvent:';
  }

  addGeofence = async geofence => {
    try {
      let storedGeofences = await this.getGeofences();
      if (storedGeofences.length) {
        storedGeofences = storedGeofences.filter(
          fence => fence.id !== geofence.id,
        );
      }
      storedGeofences.unshift(geofence);
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_KEY,
        JSON.stringify(storedGeofences),
      );
      await BackgroundGeofencing.add(geofence);
      return storedGeofences;
    } catch (error) {
      console.log(error);
      return [];
    }
  };

  getGeofences = async () => {
    try {
      let storedGeofences = await AsyncStorage.getItem(
        this.DB_STORED_GEOFENCE_KEY,
      );
      storedGeofences = storedGeofences ? JSON.parse(storedGeofences) : [];
      return storedGeofences;
    } catch (error) {
      console.log(error);
      return [];
    }
  };

  getGeofenceEvents = async geofenceId => {
    try {
      const KEY = `${this.DB_STORED_GEOFENCE_EVENT_PREFIX}${geofenceId}`;
      let storedGeofenceEvents = await AsyncStorage.getItem(KEY);
      storedGeofenceEvents = storedGeofenceEvents
        ? JSON.parse(storedGeofenceEvents)
        : [];
      return storedGeofenceEvents;
    } catch (error) {
      console.log(error);
      return [];
    }
  };

  addGeofenceEvent = async (geofenceId, event, data) => {
    try {
      const KEY = `${this.DB_STORED_GEOFENCE_EVENT_PREFIX}${geofenceId}`;
      let storedGeofenceEvents = await this.getGeofenceEvents(geofenceId);
      storedGeofenceEvents.unshift({event, data});
      await AsyncStorage.setItem(KEY, JSON.stringify(storedGeofenceEvents));
      console.log('Geofence event AsyncSaved: ', geofenceId);
    } catch (error) {
      console.log(error);
    }
  };

  removeGeofence = async geofenceId => {
    try {
      const KEY = `${this.DB_STORED_GEOFENCE_EVENT_PREFIX}${geofenceId}`;
      let storedGeofences = await this.getGeofences();
      storedGeofences = storedGeofences.filter(
        fence => fence.id !== geofenceId,
      );
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_KEY,
        JSON.stringify(storedGeofences),
      );
      console.log('removed Geofence from gen pop');
      await AsyncStorage.removeItem(KEY);
      console.log('removed Geofence events');
      await BackgroundGeofencing.remove(geofenceId);
      console.log('removed listener');
    } catch (error) {
      console.log(error);
    }
  };
}
