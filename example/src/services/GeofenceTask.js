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
      };
    });
    await repo.addGeofenceEvents(events);
  } catch (error) {
    console.log(error);
  }
}
