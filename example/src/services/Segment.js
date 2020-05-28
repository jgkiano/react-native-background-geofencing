import analytics from '@segment/analytics-react-native';
import secrets from '../../secrets.json';

export const initSegment = async () => {
  try {
    await analytics.setup(secrets.segment, {
      // Record screen views automatically!
      recordScreenViews: true,
      // Record certain application events automatically!
      trackAppLifecycleEvents: true,
    });
    console.log('segment setup complete..');
  } catch (error) {
    console.log(error);
  }
};
