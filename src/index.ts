import {
  NativeModules,
  Platform,
  PlatformOSType,
  AppRegistry,
  PermissionsAndroid,
  Rationale,
} from 'react-native';
import ms from 'ms';
import {
  RNBackgroundGeofencing,
  RNBackgroundGeofencingWebhook,
  RNBackgroundGeofencingException,
  RNBackgroundGeofencingConfiguration,
  RNBackgroundGeofencingJSTask,
  RNBackgroundGeofencingNotification,
  RNTransitionEvent,
  RNBackgroundGeofence,
} from './types';

const { BackgroundGeofencing } = NativeModules;

const SUPPORTED_PLATFORMS: Array<PlatformOSType> = ['android'];

const TAG = '[RNBackgroundGeofencing]: ';

const RNBackgroundGeofencing: RNBackgroundGeofencing = {
  ...BackgroundGeofencing,
};

const DEFAULT_WEBHOOK_CONFIGURATION = {
  url: null,
  headers: {},
  timeout: 15000,
};

const DEFAULT_NOTIFICATION_CONFIGURATION: RNBackgroundGeofencingNotification = {
  text: '',
  title: '',
  importance: 3,
  channelDescription: 'RNBackgroundGeofencing',
  channelName: 'RNBackgroundGeofencing',
  channelId: 'RNBackgroundGeofencing',
};

const DEFAULT_GEOFENCE_CONFIGURATION = {
  expiresIn: -1,
  loiteringDelay: '30m',
  notificationResponsiveness: '3m',
  registerOnDeviceRestart: true,
  transitionTypes: ['enter', 'exit', 'dwell'],
  initiainitialTriggerTransitionTypes: ['enter', 'exit', 'dwell'],
};

const withPlatformSupport = <T, E>(wrappedFunction: () => T, errorValue: E): T | E => {
  const currentPlatform = Platform.OS;
  if (SUPPORTED_PLATFORMS.includes(currentPlatform)) {
    return wrappedFunction();
  } else {
    console.warn(TAG + `This library doesn't support ${currentPlatform} devices`);
    return errorValue;
  }
};

const _configureNotification = async (notification: RNBackgroundGeofencingNotification) => {
  return RNBackgroundGeofencing.configureNotification({
    ...DEFAULT_NOTIFICATION_CONFIGURATION,
    ...notification,
  });
};

const configureJSTask = async (jsTask: RNBackgroundGeofencingJSTask) => {
  try {
    const { task, notification } = jsTask;
    if (typeof task !== 'function') {
      throw new RNBackgroundGeofencingException({
        code: 'geofence_exception',
        message: 'A valid javascript function is required to configure a background task',
      });
    }
    if (!notification?.title || !notification?.text) {
      throw new RNBackgroundGeofencingException({
        code: 'geofence_exception',
        message: 'A valid notitication title and text is required',
      });
    }
    AppRegistry.registerHeadlessTask('OnGeofenceTransitionEvent', () => {
      const headlessTask = async (transitionEvent: RNTransitionEvent) => {
        try {
          await task(JSON.parse(transitionEvent.RN_GEOFENCE_TRANSITION_DATA));
        } catch (error) {
          console.error(`[RNBackgroundGeofencing]`, error);
        }
      };
      return headlessTask;
    });
    await _configureNotification(notification);
  } catch (error) {
    throw error;
  }
};

/**
 * Configures the notification that will be displayed when the application
 * receives a geofence event and runs the js task in the foreground (Android - devices)
 * @param notification
 */
export async function configureNotification(notification: RNBackgroundGeofencingNotification) {
  return _configureNotification(notification);
}

/**
 * Configures a webhook that the device will use to send Geofence events via POST request
 * @param webhook
 */
export async function configureWebhook(webhook: RNBackgroundGeofencingWebhook) {
  return withPlatformSupport(() => {
    if (typeof webhook?.url !== 'string') {
      throw new RNBackgroundGeofencingException({
        code: 'geofence_exception',
        message: 'A valid url is required to configure a webhook',
      });
    }
    return RNBackgroundGeofencing.configureWebhook({
      ...DEFAULT_WEBHOOK_CONFIGURATION,
      ...webhook,
    });
  }, false);
}

/**
 * Checks if location permission is granted by the user
 */
export async function isLocationPermissionGranted() {
  return withPlatformSupport(() => {
    return RNBackgroundGeofencing.isLocationPermissionGranted();
  }, false);
}

/**
 * Checks if location services are enabled
 */
export async function isLocationServicesEnabled() {
  return withPlatformSupport(() => {
    return RNBackgroundGeofencing.isLocationServicesEnabled();
  }, false);
}

/**
 * Checks if Google play services are enabled (Android only)
 */
export async function isGooglePlayServicesAvailable() {
  if (Platform.OS !== 'android') {
    return false;
  }
  return RNBackgroundGeofencing.isGooglePlayServicesAvailable();
}

/**
 * Displays an in app dialog, that prompts the user to enable google play services (Android only)
 * Returns true when user enables the service
 */
export async function requestEnableGooglePlayServices() {
  if (Platform.OS !== 'android') {
    return false;
  }
  return RNBackgroundGeofencing.requestEnableGooglePlayServices();
}

/**
 * Displays an in app dialog, that prompts the user to enable location services (Android only)
 * Returns true when user enables the service. DOES NOT WORK ON SIMULATOR
 */
export async function requestEnableLocationServices() {
  if (Platform.OS !== 'android') {
    return false;
  }
  return RNBackgroundGeofencing.requestEnableLocationServices();
}

/**
 * Displays an in app dialog, that prompts the user to grant location permission (Android only)
 * Returns true when user grants the permission
 * @param rationale
 */
export async function requestLocationPermission(rationale: Rationale) {
  return withPlatformSupport(async () => {
    const hasPermission = await RNBackgroundGeofencing.isLocationPermissionGranted();
    if (hasPermission) {
      return hasPermission;
    }
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      rationale
    );
    return granted === PermissionsAndroid.RESULTS.GRANTED;
  }, false);
}

/**
 * Opens the device's location services settings
 */
export async function openLocationServicesSettings() {
  withPlatformSupport(() => {
    RNBackgroundGeofencing.openLocationServicesSettings();
  }, undefined);
}

export default {
  /**
   * Configures how you'd like to receive geofence events.
   * Either through a webhook post, a javascript task that you define or both.
   * This should be called one time on App start up
   * @param configuration
   */
  async configure(configuration: RNBackgroundGeofencingConfiguration) {
    return withPlatformSupport(async () => {
      try {
        const { webhook, jsTask } = configuration;
        if (webhook) {
          await configureWebhook(webhook);
        }
        if (jsTask) {
          await configureJSTask(jsTask);
        }
        return true;
      } catch (error) {
        throw error;
      }
    }, false);
  },

  /**
   * Performs work like silently attempting to register failed geofences in the background.
   * You should call this method in your index.js for maximum geofence relaibility on app start
   */
  init() {
    withPlatformSupport(RNBackgroundGeofencing.init, undefined);
  },

  /**
   * Attempts to re-register all geofences silently
   */
  restart() {
    withPlatformSupport(RNBackgroundGeofencing.restart, undefined);
  },

  /**
   * Adds a geofence, if successfull return the id of the registered geofence
   */
  add(geofence: RNBackgroundGeofence) {
    return withPlatformSupport(() => {
      if (!geofence?.id || !geofence?.lat || !geofence?.lng || !geofence?.radius) {
        throw new RNBackgroundGeofencingException({
          code: 'geofence_exception',
          message: 'A valid id, lat, lng and radius is required to add a geofence',
        });
      }
      let loiteringDelay: number = ms(DEFAULT_GEOFENCE_CONFIGURATION.loiteringDelay);
      let notificationResponsiveness: number = ms(
        DEFAULT_GEOFENCE_CONFIGURATION.notificationResponsiveness
      );
      let expiresIn: number = DEFAULT_GEOFENCE_CONFIGURATION.expiresIn;
      if (geofence.expiresIn) {
        expiresIn = ms(String(geofence.expiresIn));
      }
      if (geofence.loiteringDelay) {
        loiteringDelay = ms(String(geofence.loiteringDelay));
      }
      if (geofence.notificationResponsiveness) {
        notificationResponsiveness = ms(String(geofence.notificationResponsiveness));
      }
      return RNBackgroundGeofencing.add({
        ...DEFAULT_GEOFENCE_CONFIGURATION,
        ...geofence,
        loiteringDelay,
        notificationResponsiveness,
        expiresIn,
      });
    }, Promise.resolve(geofence.id));
  },

  /**
   * Removes a geofence based on the id, if successfull return the id of the removed geofence
   */
  remove(geofenceId: string) {
    return withPlatformSupport(() => {
      return RNBackgroundGeofencing.remove(geofenceId);
    }, Promise.resolve(geofenceId));
  },
};
