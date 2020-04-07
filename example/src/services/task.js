import Repository from './Repository';

export default async function task({event, data}) {
  console.log('--client--');
  console.log(event);
  console.log(typeof event);
  console.log(data);
  console.log(typeof data);
  console.log('----------');
  try {
    const repo = new Repository();
    const locationData = JSON.parse(data);
    if (locationData.geofenceIds && locationData.geofenceIds.length) {
      const promises = [];
      locationData.geofenceIds.forEach(id => {
        promises.push(
          repo.addGeofenceEvent(id, event, {
            ...locationData,
            geofenceIds: undefined,
          }),
        );
      });
      await Promise.all(promises);
    }
  } catch (error) {
    console.error(error);
  }
}
