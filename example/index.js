import {AppRegistry} from 'react-native';
import {configure} from 'react-native-background-geofencing';
import App from './App';
import {name as appName} from './app.json';
import task from './src/services/task';
import secrets from './secrets.json';

configure({
  notification: {
    title: 'Starting set up',
    text: 'This will only take a sec..',
    importance: 3,
    channelId: 'myChannelId',
    channelName: 'My Channel Name',
    channelDescription: 'My channel description',
  },
  webhook: {
    url: secrets.webhook,
    meta: {
      lib: {
        name: 'foo',
        version: 'bar',
      },
    },
  },
  jsTask: {
    task,
  },
});

AppRegistry.registerComponent(appName, () => App);
