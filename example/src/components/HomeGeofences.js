/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {TouchableNativeFeedback, View, Text, FlatList} from 'react-native';

export default function HomeGeofences(props) {
  const renderItem = ({item}) => {
    return (
      <TouchableNativeFeedback onPress={() => props.onItemTap(item.id)}>
        <View
          style={{
            marginHorizontal: 15,
            borderRadius: 4,
            padding: 15,
            backgroundColor: 'white',
            elevation: 1,
          }}>
          <Text style={{fontSize: 18}}>{`${item.id}`}</Text>
          <Text style={{marginTop: 20, fontSize: 12, opacity: 0.5}}>{`${
            item.lat
          }, ${item.lng}`}</Text>
          <Text style={{fontSize: 12, opacity: 0.5}}>{`${
            item.radius
          }m radius`}</Text>
          <Text
            style={{
              fontSize: 12,
              opacity: 0.5,
            }}>{`Register Geopoint on device start: ${
            item.registerOnDeviceRestart
          }`}</Text>
        </View>
      </TouchableNativeFeedback>
    );
  };
  return (
    <FlatList
      style={{paddingVertical: 15, flex: 1}}
      data={props.geofences}
      renderItem={renderItem}
      keyExtractor={item => item.id}
      ItemSeparatorComponent={() => <View style={{padding: 5}} />}
    />
  );
}
