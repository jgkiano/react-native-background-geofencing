/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {Platform} from 'react-native';
import {
  TouchableNativeFeedback,
  View,
  Text,
  TouchableOpacity,
} from 'react-native';

export default function FullButton(props) {
  const Touchable = Platform.select({
    ios: TouchableOpacity,
    android: TouchableNativeFeedback,
  });
  return (
    <Touchable {...props}>
      <View
        style={{
          height: 54,
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: props.disabled
            ? '#BDBDBD'
            : props.color || '#1565C0',
          ...props.style,
        }}>
        <Text style={{color: 'white', fontWeight: 'bold', fontSize: 16}}>
          {props.label}
        </Text>
      </View>
    </Touchable>
  );
}
