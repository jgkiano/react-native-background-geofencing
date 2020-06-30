import React from 'react';
import {Platform} from 'react-native';
import {request, PERMISSIONS} from 'react-native-permissions';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';

import HomeScreen from './src/screens/Home';
import HistoryScreen from './src/screens/History';

const Stack = createStackNavigator();

class App extends React.Component {
  constructor(props) {
    super(props);
    // this.askPermissions();
  }

  askPermissions = async () => {
    try {
      const rational = {
        title: 'OkHi VaaS needs location permission',
        message:
          'We need location permission to enable us to create and verify the addresses you create',
        buttonPositive: 'GRANT',
        buttonNegative: 'DENY',
        buttonNeutral: 'CANCEL',
      };
      if (Platform.OS === 'android') {
        await request(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION, rational);
        await request(PERMISSIONS.ANDROID.ACCESS_BACKGROUND_LOCATION, rational);
      } else {
        return false;
      }
    } catch (error) {}
  };

  render() {
    return (
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen name="Geofences" component={HomeScreen} />
          <Stack.Screen name="History" component={HistoryScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    );
  }
}

export default App;
