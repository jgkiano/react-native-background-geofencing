import Repository from './Repository';
import uuid from 'react-native-uuid';

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
  } catch (error) {
    console.log(error);
  }
}
