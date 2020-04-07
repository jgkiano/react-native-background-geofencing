/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {View, Text, KeyboardAvoidingView} from 'react-native';
import validator from 'validator';
import CheckBox from '@react-native-community/checkbox';
import {ScrollView} from 'react-native-gesture-handler';
import FullButton from './FullButton';
import InputItem from './InputItem';

export default class ModalBody extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      id: '',
      lat: '',
      lng: '',
      radius: '',
      expiration: '-1',
      registerOnDeviceRestart: true,
      setDwellTransitionType: false,
      setInitialTriggers: true,
      loiteringDelay: '0',
      error: null,
    };
  }
  handleSubmit = () => {
    const {
      id,
      lat,
      lng,
      radius,
      expiration,
      registerOnDeviceRestart,
      setDwellTransitionType,
      setInitialTriggers,
      loiteringDelay,
    } = this.state;
    this.setState({error: null});
    if (!validator.isAlpha(id)) {
      return this.setState({error: 'Please provide a valid Geofence ID'});
    }
    if (!validator.isLatLong(`${lat},${lng}`)) {
      console.log(lat);
      return this.setState({error: 'Please provide coordinates'});
    }
    if (!validator.isInt(radius, {min: 150, max: 1000})) {
      return this.setState({
        error: 'Please provide a valid radius with range 150m - 1000m',
      });
    }
    if (expiration !== '-1' && !validator.isInt(expiration, {min: 60000})) {
      return this.setState({
        error:
          'Please provide a valid expiration limit min value: 60000 (1 minute)',
      });
    }
    if (
      loiteringDelay !== '0' &&
      !validator.isInt(loiteringDelay, {min: 300000})
    ) {
      return this.setState({
        error:
          'Please provide a valid loitering delay limit min value: 300000 (5 minutes)',
      });
    }
    this.props.onSubmit({
      id,
      lat: parseFloat(lat),
      lng: parseFloat(lng),
      radius: parseFloat(lng),
      expiration: expiration === '-1' ? -1 : Number(expiration),
      registerOnDeviceRestart,
      setDwellTransitionType,
      setInitialTriggers,
      loiteringDelay: Number(loiteringDelay),
    });
  };
  handleChangeText = (type, text = '') => {
    switch (type) {
      case 'id':
        if (
          !text.length ||
          (validator.isAlpha(text) && validator.isLowercase(text))
        ) {
          this.setState({id: text.trim()});
        }
        return;
      default:
        this.setState({[type]: text.trim()});
        break;
    }
  };
  render() {
    const {
      id,
      lat,
      lng,
      registerOnDeviceRestart,
      setDwellTransitionType,
      loiteringDelay,
      radius,
      expiration,
      error,
    } = this.state;
    return (
      <View style={{flex: 1, backgroundColor: 'white'}}>
        <View style={{flex: 1}}>
          <ScrollView>
            <KeyboardAvoidingView behavior="padding" style={{flex: 1}}>
              <Text
                style={{
                  paddingHorizontal: 15,
                  paddingTop: 15,
                  fontSize: 28,
                  fontWeight: 'bold',
                  opacity: 0.8,
                }}>
                Add a Geofence
              </Text>
              <View style={{paddingHorizontal: 15, marginTop: 15}}>
                <InputItem
                  label="Geofence ID"
                  placeholder="kianocrib"
                  hint="lowercase, one word, no spaces, unique"
                  onChangeText={text => this.handleChangeText('id', text)}
                  value={id}
                />
              </View>
              <View style={{paddingHorizontal: 15, marginTop: 15}}>
                <View style={{flexDirection: 'row'}}>
                  <InputItem
                    label="Latitude"
                    placeholder="-1.314678"
                    hint="numbers only"
                    onChangeText={text => this.handleChangeText('lat', text)}
                    keyboardType="numeric"
                    value={lat}
                  />
                  <View style={{marginHorizontal: 5}} />
                  <InputItem
                    label="Longitude"
                    placeholder="36.836327"
                    hint="numbers only"
                    onChangeText={text => this.handleChangeText('lng', text)}
                    keyboardType="numeric"
                    value={lng}
                  />
                </View>
              </View>

              <View style={{paddingHorizontal: 15, marginTop: 15}}>
                <View style={{flexDirection: 'row'}}>
                  <InputItem
                    label="Radius (m)"
                    placeholder="500"
                    hint="Circular radius of the geofence in meters"
                    onChangeText={text => this.handleChangeText('radius', text)}
                    keyboardType="numeric"
                    value={radius}
                  />
                </View>

                <View
                  style={{
                    flexDirection: 'row',
                    alignItems: 'center',
                    marginTop: 15,
                  }}>
                  <CheckBox
                    value={registerOnDeviceRestart}
                    onValueChange={value =>
                      this.setState({
                        registerOnDeviceRestart: value,
                        error: null,
                      })
                    }
                  />
                  <Text>Register Geofence on device restart</Text>
                </View>

                <View
                  style={{
                    flexDirection: 'row',
                    alignItems: 'center',
                    marginTop: 15,
                  }}>
                  <CheckBox
                    value={expiration !== '-1'}
                    onValueChange={value =>
                      this.setState({
                        expiration: value ? '60000' : '-1',
                        error: null,
                      })
                    }
                  />
                  <Text>Should expire</Text>
                </View>

                {expiration !== '-1' ? (
                  <View style={{marginTop: 15}}>
                    <InputItem
                      label="Expiration (ms)"
                      placeholder="300000"
                      hint="Time in milliseconds that the geofence should stay active. 1m = 60000ms"
                      keyboardType="numeric"
                      onChangeText={text =>
                        this.handleChangeText('expiration', text)
                      }
                      value={expiration}
                    />
                  </View>
                ) : null}

                <View
                  style={{
                    flexDirection: 'row',
                    alignItems: 'center',
                    marginTop: 15,
                  }}>
                  <CheckBox
                    value={setDwellTransitionType}
                    disabled={false}
                    onValueChange={value =>
                      this.setState({
                        setDwellTransitionType: value,
                        loiteringDelay: value === true ? '300000' : '0',
                        error: null,
                      })
                    }
                  />
                  <Text>Track dwell transitions (BETA)</Text>
                </View>
                {setDwellTransitionType ? (
                  <View style={{marginTop: 15}}>
                    <InputItem
                      label="Loitering delay (ms)"
                      placeholder="300000"
                      keyboardType="numeric"
                      hint="if loitering delay is set to 300000 ms geofence service will send a loitering alert roughly 5 minutes after user enters a geofence and if the user stays inside the geofence during this period of time."
                      onChangeText={text =>
                        this.handleChangeText('loiteringDelay', text)
                      }
                      value={loiteringDelay}
                    />
                  </View>
                ) : null}
                {error ? (
                  <Text
                    style={{
                      backgroundColor: '#ffcdd2',
                      color: '#b71c1c',
                      paddingVertical: 10,
                      paddingHorizontal: 15,
                      borderRadius: 8,
                      marginTop: 15,
                    }}>
                    {error}
                  </Text>
                ) : null}
              </View>
            </KeyboardAvoidingView>
          </ScrollView>
        </View>
        <FullButton color="#00796B" label="SAVE" onPress={this.handleSubmit} />
      </View>
    );
  }
}
