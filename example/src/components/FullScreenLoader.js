/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import _ from 'lodash';
import {View, ActivityIndicator, Text} from 'react-native';

export default function FullScreenLoader(props) {
  const messages = [
    'Locating the required gigapixels to render...',
    'Spinning up the hamster',
    'Shovelling coal into the server...',
    'Charging the flux capacitor',
    'Waiting for scotty to beam me up',
    'Pay no attention to the man behind the curtain',
    'Enjoy the elevator music (づ￣ ³￣)づ',
    'A few bits tried to escape, but we caught them',
    'Would you like fries with that?',
    'Go ahead -- hold your breath',
    'The server is powered by a lemon and two electrodes',
    'We love you just the way you are ♥️',
    'Testing your patience...',
    'Stopping gremlins...',
    'Insert a quarter to continue',
    'Waiting for the system admin to hit enter...',
    'Your underwear has conflicted our DB. Please change daily.',
    'Wash your damn hands',
    'Come here often?',
    'Taking the red pill...',
    'Have you tried turning it off and on again?',
    "What you talkin' 'bout, Willis? (-_-)",
    'Hey there (^-^)/',
    'Oooh yeaahhh ( ͡° ͜ʖ ͡°)',
    'Who has two thumbs and is loading..this guy! (☞ﾟ∀ﾟ)☞',
    'Senpai notice me (◕‿◕✿)',
  ];

  return (
    <View
      style={{
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: 30,
      }}>
      <ActivityIndicator size="large" color="#42A5F5" />
      <Text
        style={{
          fontSize: 16,
          marginTop: 15,
          opacity: 0.5,
          textAlign: 'center',
        }}>
        {_.shuffle(messages)[0]}
      </Text>
    </View>
  );
}
