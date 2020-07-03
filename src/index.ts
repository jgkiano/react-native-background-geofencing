import { NativeModules, Platform, PlatformOSType } from 'react-native';
import {
  RNBackgroundGeofencing,
  RNBackgroundGeofencingWebhook,
  RNBackgroundGeofencingException,
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

const withPlatformSupport = <T, E>(
  wrappedFunction: () => T,
  errorValue: E
): T | E => {
  const currentPlatform = Platform.OS;
  if (SUPPORTED_PLATFORMS.includes(currentPlatform)) {
    return wrappedFunction();
  } else {
    console.warn(
      TAG + `This library doesn't support ${currentPlatform} devices`
    );
    return errorValue;
  }
};

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
