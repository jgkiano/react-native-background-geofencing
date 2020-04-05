import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Button} from 'react-native';
import BackgroundGeofencing from 'react-native-background-geofencing';
import {request, check, PERMISSIONS} from 'react-native-permissions';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.askPermissions();
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
      } else if (Platform.OS === 'ios') {
        await request(PERMISSIONS.IOS.LOCATION_ALWAYS, rational);
        await request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE, rational);
      } else {
        return false;
      }
    } catch (error) {}
  };

  startGeofence = async () => {
    try {
      const addResult = await BackgroundGeofencing.add({
        id: 'kianoshome',
        lat: -1.314683,
        lng: 36.836333,
        radius: 300,
        registerOnDeviceRestart: true,
      });
      console.log(addResult, 'addResult');
    } catch (error) {
      console.log(error);
    }
  };

  removeGeofence = async () => {
    BackgroundGeofencing.remove('kianoshome');
  };

  render() {
    return (
      <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
        <Text>Welcome to geofencing</Text>
        <View style={{marginTop: 15}}>
          <Button title="Start Geofence" onPress={this.startGeofence} />
        </View>
        <View style={{marginTop: 15}}>
          <Button title="Remove Geofence" onPress={this.removeGeofence} />
        </View>
      </View>
    );
  }
}

export default App;
