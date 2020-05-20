import AsyncStorage from '@react-native-community/async-storage';
import RNBackgroundGeofencing from 'react-native-background-geofencing';

export default class Repository {
  constructor() {
    this.DB_STORED_USER = '@user';
    this.DB_STORED_GEOFENCE_KEY = '@storedGeofences';
    this.DB_STORED_GEOFENCE_EVENT_PREFIX = '@storedGeofenceEvent:';
  }

  getUser = async () => {
    try {
      const user = await AsyncStorage.getItem(this.DB_STORED_USER);
      if (user) {
        return JSON.parse(user);
      } else {
        return null;
      }
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  addUser = async user => {
    try {
      await AsyncStorage.setItem(this.DB_STORED_USER, JSON.stringify(user));
    } catch (error) {
      console.error(error);
    }
  };

  addGeofence = async geofence => {
    try {
      await RNBackgroundGeofencing.add(geofence.configuration);
      const geofences = (await this.getGeofences()) || [];
      geofences.unshift(geofence);
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_KEY,
        JSON.stringify(geofences),
      );
      console.log(
        'geofence set and saved with configutation:\n',
        geofence.configuration,
      );
      return geofences;
    } catch (error) {
      console.log(geofence.configuration);
      throw error;
    }
  };

  getGeofences = async () => {
    try {
      const geofences = await AsyncStorage.getItem(this.DB_STORED_GEOFENCE_KEY);
      if (geofences) {
        return JSON.parse(geofences);
      } else {
        return [];
      }
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

  // addGeofenceEvent = async (geofenceId, event, data) => {
  //   try {
  //     const KEY = `${this.DB_STORED_GEOFENCE_EVENT_PREFIX}${geofenceId}`;
  //     let storedGeofenceEvents = await this.getGeofenceEvents(geofenceId);
  //     storedGeofenceEvents.unshift({event, data});
  //     await AsyncStorage.setItem(KEY, JSON.stringify(storedGeofenceEvents));
  //     console.log('Geofence event AsyncSaved: ', geofenceId);
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };

  // removeGeofence = async geofenceId => {
  //   try {
  //     const KEY = `${this.DB_STORED_GEOFENCE_EVENT_PREFIX}${geofenceId}`;
  //     let storedGeofences = await this.getGeofences();
  //     storedGeofences = storedGeofences.filter(
  //       fence => fence.id !== geofenceId,
  //     );
  //     await AsyncStorage.setItem(
  //       this.DB_STORED_GEOFENCE_KEY,
  //       JSON.stringify(storedGeofences),
  //     );
  //     console.log('removed Geofence from gen pop');
  //     await AsyncStorage.removeItem(KEY);
  //     console.log('removed Geofence events');
  //     await RNBackgroundGeofencing.remove(geofenceId);
  //     console.log('removed listener');
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };
}
