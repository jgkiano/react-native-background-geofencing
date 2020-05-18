import {AppRegistry} from 'react-native';
import {
  configureJSTask,
  configureWebhook,
} from 'react-native-background-geofencing';
import App from './App';
import {name as appName} from './app.json';
import task from './src/services/task';
import secrets from './secrets.json';

configureJSTask({
  task,
  notification: {
    title: 'Geofence warming up',
    text: 'Registering geofences...',
  },
});

configureWebhook({
  url: secrets.webhook,
  meta: {
    lib: {
      name: 'foo',
      version: 'bar',
    },
  },
});

AppRegistry.registerComponent(appName, () => App);
