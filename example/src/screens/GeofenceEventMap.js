import React from 'react';
import MapView, {PROVIDER_GOOGLE} from 'react-native-maps';
import {Text, View} from 'react-native';

export default function GeofenceEventMapScreen({route}) {
  return (
    <View style={{flex: 1}}>
      <MapView
        provider={PROVIDER_GOOGLE} // remove if not using Google Maps
        style={{flex: 1}}
        region={{
          latitude: 37.78825,
          longitude: -122.4324,
          latitudeDelta: 0.015,
          longitudeDelta: 0.0121,
        }}
      />
    </View>
  );
}
