import uuid from 'react-native-uuid';
import analytics from '@segment/analytics-react-native';
import {initSegment} from './Segment';
import Repository from './Repository';

export default async function GeofenceTask({event, data}) {
  try {
    const repo = new Repository();
    const ids = data.geofenceIds;
    delete data.geofenceIds;
    const events = ids.map(id => {
      return {
        ...data,
        id,
        event: event.toLowerCase(),
        uuid: uuid.v4(),
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
