/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {View, Text} from 'react-native';
import {TextInput} from 'react-native-gesture-handler';

export default function InputItem(props) {
  return (
    <View style={{flex: 1}}>
      <Text style={{fontSize: 16, fontWeight: 'bold', marginBottom: 5}}>
        {props.label}
      </Text>
      <TextInput
        style={{
          borderWidth: 2,
          borderRadius: 8,
          borderColor: '#E0E0E0',
          height: 46,
          padding: 15,
          fontSize: 16,
        }}
        autoCapitalize="none"
        autoCompleteType="off"
        autoCorrect={false}
        {...props}
      />
      <Text style={{opacity: 0.3, fontSize: 12}}>{props.hint}</Text>
    </View>
  );
}
