import {AppRegistry} from 'react-native';
// import {
//   configureWebhook,
//   configureJSTask,
// } from 'react-native-background-geofencing';
import {configureWebhook} from 'react-native-background-geofencing';
import {initSegment} from './src/services/Segment';
import App from './App';
import {name as appName} from './app.json';
import secrets from './secrets.json';
import GeofenceTask from './src/services/GeofenceTask';

console.log('sdlfjsldjflskdjflskjdflskjdflskjdflkjsldjflkjskdf');

// configureWebhook({url: secrets.webhook}).then(() => {
//   console.log('hmm...');
// });

// initSegment();

// configureJSTask({
//   notification: {
//     title: 'New geofence event',
//     text: 'Processing geofence event..',
//   },
//   task: GeofenceTask,
// });

AppRegistry.registerComponent(appName, () => App);
