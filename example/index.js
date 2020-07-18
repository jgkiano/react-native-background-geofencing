import {AppRegistry} from 'react-native';
import RNBackgroundGeofencing from 'react-native-background-geofencing';
import {initSegment} from './src/services/Segment';
import App from './App';
import {name as appName} from './app.json';
import task from './src/services/task';
// import secrets from './secrets.json';

initSegment();

RNBackgroundGeofencing.configure({
  jsTask: {
    task,
    notification: {
      title: 'Work work',
      text: 'Get that money',
    },
  },
});

AppRegistry.registerComponent(appName, () => App);
