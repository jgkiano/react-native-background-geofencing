import AsyncStorage from '@react-native-community/async-storage';
import RNBackgroundGeofencing from 'react-native-background-geofencing';

export default class Repository {
  constructor() {
    this.DB_STORED_USER = '@user';
    this.DB_STORED_GEOFENCE_KEY = '@storedGeofences';
    this.DB_STORED_GEOFENCE_EVENTS_KEY = '@storedGeofenceEvents';
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
      geofence.configuration.createdAt = Date.now();
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

  addGeofenceEvents = async (events = []) => {
    try {
      const existingEvents = await this.getGeofenceEvents();
      const newEvents = [...events, ...existingEvents];
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_EVENTS_KEY,
        JSON.stringify(newEvents),
      );
      console.log('saved geofence events successfully');
    } catch (error) {
      throw error;
    }
  };

  getGeofenceEvents = async () => {
    try {
      let events = await AsyncStorage.getItem(
        this.DB_STORED_GEOFENCE_EVENTS_KEY,
      );
      events = events ? JSON.parse(events) : [];
      return events;
    } catch (error) {
      throw error;
    }
  };

  removeGeofenceEvent = async uuid => {
    try {
      let events = await this.getGeofenceEvents();
      events = events.filter(event => event.uuid !== uuid);
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_EVENTS_KEY,
        JSON.stringify(events),
      );
    } catch (error) {
      throw error;
    }
  };

  removeGeofence = async geofenceId => {
    try {
      const geofenceEvents = await this.getGeofenceEvents();
      const geofences = await this.getGeofences();
      const filteredEvents = geofenceEvents.filter(
        event => event.id !== geofenceId,
      );
      const filteredGeofences = geofences.filter(
        geofence => geofence.configuration.id !== geofenceId,
      );
      // await RNBackgroundGeofencing.remove(geofenceId);
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_EVENTS_KEY,
        JSON.stringify(filteredEvents),
      );
      await AsyncStorage.setItem(
        this.DB_STORED_GEOFENCE_KEY,
        JSON.stringify(filteredGeofences),
      );
      console.log('successfully removed geofence');
      return {
        geofences: filteredGeofences,
        events: filteredEvents,
      };
    } catch (error) {
      throw error;
    }
  };
}
