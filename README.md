# React Native Background Geofencing

A reliable React Native geofencing library, that works in the background and survives device restarts.

**WARNING: Currently only supports Android devices. Plans for iOS are underway**

## Getting started

`$ yarn add react-native-background-geofencing`

### Android

#### Mostly automatic installation (for RN < 0.60)

`$ react-native link react-native-background-geofencing`

#### Permissions

Add the following permissions to your `AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
  <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.WAKE_LOCK" />

</manifest>
```

## Usage

#### 1. Create an async JavaScript task file that will handle Geofence events

`$ touch myTask.js`

#### 2. Place the following code inside `myTask.js`

The task will be executed in the background even after the device restarts.

```javascript
import {RNGeofenceEvent} from 'react-native-background-geofencing';
export default async function myTask({event, data}) {
  // handle Geofence updates here
  if (event === RNGeofenceEvent.ENTER) {
    console.log(`welcome to ${data.lat},${data.lng}`);
  }
  if (event === RNGeofenceEvent.ERROR) {
    console.log(`Opps, something went wrong: ${data.errorMessage}`);
  }
}
```

### 3. Configure your JavaScript task and/or webhook

Add the following in your app's `index.js` file.

```javascript
import {AppRegistry} from 'react-native';
import {
  configureJSTask,
  configureWebhook,
} from 'react-native-background-geofencing';
import App from './App';
import myTask from './myTask.js';

configureJSTask({
  task: myTask,
  // Required to enable your task to run as an Android foreground service
  notification: {
    title: "Don't mind me",
    text: "I'm just a notification",
  },
});

// If you'd like to ship the events to a server use this.
// Its guaranteed to run when the device has an internet connection using Android's Work manager API

configureWebhook({
  url: 'https://myapi.com/geofences',
  headers: {
    foo: 'Bar',
  },
});

AppRegistry.registerComponent(appName, () => App);
```

### 4. Add / Remove Geofences

```javascript
import RNBackgroundGeofencing from 'react-native-background-geofencing';

const geofence = {
  id: 'mygeofence',
  lat: -1.29273,
  lng: 36.820389,
};

await RNBackgroundGeofencing.add(geofence);

await RNBackgroundGeofencing.remove(geofence.id);
```

## Contributions

Are welcomed ♥️ especially those specifically addressing iOS support and bug fixes.
