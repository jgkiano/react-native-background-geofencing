import {AppRegistry} from 'react-native';
import {
  configureJSTask,
  configureWebhook,
} from 'react-native-background-geofencing';
import App from './App';
import {name as appName} from './app.json';
import geofenceTask from './geofence-task';

configureJSTask({
  task: geofenceTask,
  notification: {
    title: 'Geofence warming up',
    text: 'Registering geofences...',
  },
});

configureWebhook({
  url: 'http://192.168.1.97:4000/geofence',
  headers: {
    foo: 'bar',
  },
  exclude: ['altitude'],
});

AppRegistry.registerComponent(appName, () => App);
