import React from 'react';
import {Platform} from 'react-native';
// import {request, PERMISSIONS} from 'react-native-permissions';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import {Provider} from './src/context';
import {requestLocationPermission} from 'react-native-background-geofencing';

import HomeScreen from './src/screens/Home';
import HistoryScreen from './src/screens/History';
import LoginScreen from './src/screens/Login';
import InitScreen from './src/screens/Init';
import AddGeofenceScreen from './src/screens/AddGeofence';
import GeofenceHistoryScreen from './src/screens/GeofenceHistory';
import GeofenceEventMapScreen from './src/screens/GeofenceEventMap';

const Stack = createStackNavigator();

class App extends React.Component {
  constructor(props) {
    super(props);
    requestLocationPermission();
  }

  // askPermissions = async () => {
  //   try {
  //     const rational = {
  //       title: 'OkHi VaaS needs location permission',
  //       message:
  //         'We need location permission to enable us to create and verify the addresses you create',
  //       buttonPositive: 'GRANT',
  //       buttonNegative: 'DENY',
  //       buttonNeutral: 'CANCEL',
  //     };
  //     if (Platform.OS === 'android') {
  //       await request(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION, rational);
  //       await request(PERMISSIONS.ANDROID.ACCESS_BACKGROUND_LOCATION, rational);
  //     } else {
  //       return false;
  //     }
  //   } catch (error) {}
  // };

  render() {
    return (
      <Provider>
        <NavigationContainer>
          <Stack.Navigator>
            <Stack.Screen
              options={{header: () => null}}
              name="Init"
              component={InitScreen}
            />
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="History" component={HistoryScreen} />
            <Stack.Screen
              options={{title: 'Add a Geofence'}}
              name="AddGeofence"
              component={AddGeofenceScreen}
            />
            <Stack.Screen
              name="GeofenceHistory"
              component={GeofenceHistoryScreen}
            />
            <Stack.Screen
              options={{title: 'Map Visualization'}}
              name="GeofenceEventMap"
              component={GeofenceEventMapScreen}
            />
          </Stack.Navigator>
        </NavigationContainer>
      </Provider>
    );
  }
}

export default App;
