import {AppRegistry} from 'react-native';
import {configureWebhook} from 'react-native-background-geofencing';
import App from './App';
import {name as appName} from './app.json';
import secrets from './secrets.json';

configureWebhook({url: secrets.webhook});

AppRegistry.registerComponent(appName, () => App);
