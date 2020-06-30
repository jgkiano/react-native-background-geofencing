import {AppRegistry} from 'react-native';
import RNBackgroundGeofencing, {
  isGooglePlayServicesAvailable,
  requestEnableGooglePlayServices,
  isLocationPermissionGranted,
  requestLocationPermission,
  isLocationServicesEnabled,
  requestEnableLocationServices,
} from 'react-native-background-geofencing';
import App from './App';
import {name as appName} from './app.json';
import task from './src/services/task';
import secrets from './secrets.json';

RNBackgroundGeofencing.init();

async function startUp() {
  const playServicesStatus = await isGooglePlayServicesAvailable();
  if (!playServicesStatus) {
    console.log(
      'Play services status: ' + (await requestEnableGooglePlayServices()),
    );
  }
  const locationServicesStatus = await isLocationServicesEnabled();
  if (!locationServicesStatus) {
    console.log(
      'Location services status: ' + (await requestEnableLocationServices()),
    );
  }
  const locationPermissionStatus = await isLocationPermissionGranted();
  if (!locationPermissionStatus) {
    console.log(
      'Location services status: ' + (await requestLocationPermission()),
    );
  }
}

startUp();

const notification = {
  title: 'Starting set up',
  text: 'This will only take a sec..',
  importance: 3,
  channelId: 'myChannelId',
  channelName: 'My Channel Name',
  channelDescription: 'My channel description',
};

const webhook = {
  url: secrets.webhook,
  meta: {
    lib: {
      name: 'foo',
      version: 'bar',
    },
  },
};

const jsTask = {
  task,
};

RNBackgroundGeofencing.configure({
  notification,
  webhook,
  jsTask,
});

AppRegistry.registerComponent(appName, () => App);
