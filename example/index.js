import {AppRegistry} from 'react-native';
import {} from 'react-native-background-geofencing';
import {initSegment} from './src/services/Segment';
import App from './App';
import {name as appName} from './app.json';
// import secrets from './secrets.json';

initSegment();

AppRegistry.registerComponent(appName, () => App);
