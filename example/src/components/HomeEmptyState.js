/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {View, Text} from 'react-native';

export default function HomeEmptyState(props) {
  return (
    <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
      <Text style={{fontSize: 38, fontWeight: 'bold', color: '#333'}}>
        Getting Started 🚀
      </Text>
      <Text style={{fontSize: 18, fontWeight: '600'}}>
        Its easy..just add a Geofence
      </Text>
    </View>
  );
}
