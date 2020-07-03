import Repository from './Repository';
// import {RNGeofenceEvent} from 'react-native-background-geofencing';

export default async function({event, data}) {
  // console.log('--client--');
  // console.log(event);
  // console.log(data);
  // console.log('----------');
  // if (event === RNGeofenceEvent.EXIT) {
  //   console.log('bye kid');
  // }
  // if (event === RNGeofenceEvent.ENTER) {
  //   console.log('welcome kid');
  // }
  // try {
  //   const repo = new Repository();
  //   if (data.geofenceIds && data.geofenceIds.length) {
  //     const promises = [];
  //     data.geofenceIds.forEach(id => {
  //       promises.push(
  //         repo.addGeofenceEvent(id, event, {
  //           ...data,
  //           geofenceIds: undefined,
  //         }),
  //       );
  //     });
  //     await Promise.all(promises);
  //   }
  // } catch (error) {
  //   console.error(error);
  // }
}
