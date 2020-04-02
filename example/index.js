/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';
import {configure} from 'react-native-background-geofencing';

configure({
  jsTask: ({EVENT_NAME, EVENT_DATA}) => {
    console.log('---CLIENT---');
    console.log(EVENT_NAME);
    console.log(EVENT_DATA);
    console.log('------------');
  },
  webhook: {
    url: 'http://192.168.100.190:4000/geofence',
    headers: {
      foo: 'bar',
    },
    timeout: 20000,
    exclude: ['altitude'],
  },
});

AppRegistry.registerComponent(appName, () => App);
