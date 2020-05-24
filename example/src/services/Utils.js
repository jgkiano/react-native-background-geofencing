import axios from 'axios';
import {Platform} from 'react-native';
import {
  getSystemVersion,
  getManufacturer,
  getModel,
} from 'react-native-device-info';
import webhooks from '../../secrets.json';

export const wait = (time = 1000) => {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve();
    }, time);
  });
};

export const createGeofenceEvent = async (geofenceEvent = {}) => {
  try {
    const {
      id,
      time,
      provider,
      lat,
      lng,
      accuracy,
      event,
      source,
    } = geofenceEvent;
    const {createGeofenceEventUrl} = webhooks;
    const payload = {
      transits: [
        {
          ids: [id],
          transition_date: time,
          geopoint_provider: provider,
          geo_point: {
            lon: lng,
            lat,
          },
          gps_accuracy: accuracy,
          transition_event: event,
          geo_point_source: source || 'geofence',
          device_os_name: Platform.OS,
          device_os_version: getSystemVersion(),
          device_manufacturer: await getManufacturer(),
          device_model: getModel(),
        },
      ],
    };
    const {data} = await axios.post(createGeofenceEventUrl, payload);
    return data;
  } catch (error) {
    console.log('ERROR - Submitting event');
    console.log(JSON.stringify(error.response));
    throw error;
  }
};

export const sendGeofenceEventReview = async (avdId, review, configuration) => {
  try {
    const payload = {
      address_verification_id: avdId,
      ...review,
      geofence_confguration: configuration,
    };
    const {updateGeofenceEventUrl} = webhooks;
    await axios.post(updateGeofenceEventUrl, payload);
  } catch (error) {
    console.log('ERROR - Submitting review');
    console.log(JSON.stringify(error.response));
    throw error;
  }
};
