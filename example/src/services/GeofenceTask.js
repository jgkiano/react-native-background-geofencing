import uuid from 'react-native-uuid';
import {getPowerState, isLocationEnabled} from 'react-native-device-info';
import analytics from '@segment/analytics-react-native';
import {initSegment} from './Segment';
import Repository from './Repository';

export default async function GeofenceTask({event, data}) {
  try {
    const repo = new Repository();
    const ids = data.geofenceIds;
    delete data.geofenceIds;
    const locationEnabled = await isLocationEnabled();
    let powerState = await getPowerState();
    powerState = {
      ...powerState,
      batteryLevel: Math.floor(powerState.batteryLevel * 100),
    };
    const events = ids.map(id => {
      return {
        id,
        event: event.toLowerCase(),
        uuid: uuid.v4(),
        isLocationEnabled: locationEnabled,
        ...data,
        ...powerState,
      };
    });
    await repo.addGeofenceEvents(events);
    await initSegment();
    const tracking = events.map(geofenceEvent =>
      analytics.track('Geofence Event Triggered', {
        ...geofenceEvent,
        geoPoint: `${geofenceEvent.lat},${geofenceEvent.lng}`,
      }),
    );
    await Promise.all(tracking);
  } catch (error) {
    console.log(error);
  }
}
