/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {TouchableNativeFeedback, View, Text} from 'react-native';

export default function FullButton(props) {
  return (
    <TouchableNativeFeedback {...props}>
      <View
        style={{
          height: 54,
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: props.disabled
            ? '#BDBDBD'
            : props.color || '#1565C0',
        }}>
        <Text style={{color: 'white', fontWeight: 'bold', fontSize: 16}}>
          {props.label}
        </Text>
      </View>
    </TouchableNativeFeedback>
  );
}
