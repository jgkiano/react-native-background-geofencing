/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {Alert} from 'react-native';
import styled from 'styled-components';
import validator from 'validator';
import CheckBox from '@react-native-community/checkbox';
import {ScrollView, TouchableOpacity, View} from 'react-native';
import core from '../services/OkHiCore';
import OkHiLocationManager from '@okhi/okcollect-manager-react-native';
import InputItem from '../components/InputItem';
import FullButton from '../components/FullButton';
import FullScreenLoader from '../components/FullScreenLoader';
import {withContext} from '../context';

class AddGeofenceScreen extends React.Component {
  state = {
    address: null,
    launchOkHi: false,
    id: null,
    lat: null,
    lng: null,
    registerOnDeviceRestart: true,
    radius: '',
    title: '',
    subtitle: '',
    loiteringDelay: '',
    notificationResponsiveness: '',
    expiration: -1,
    transitionTypes: [],
    initialTriggerTransitionTypes: [],
    error: '',
  };

  handleSubmit = () => {
    const {context, navigation} = this.props;
    const {addGeofence} = context;
    let {
      id,
      lat,
      lng,
      radius,
      loiteringDelay,
      registerOnDeviceRestart,
      notificationResponsiveness,
      expiration,
      transitionTypes,
      initialTriggerTransitionTypes,
      address,
    } = this.state;
    radius = validator.toInt(radius);
    loiteringDelay = validator.toInt(loiteringDelay);
    notificationResponsiveness = validator.toInt(notificationResponsiveness);
    expiration =
      typeof expiration === 'string' ? validator.toInt(expiration) : expiration;

    if (!id || !lat || !lng) {
      this.setState(
        {error: 'Please select an OkHi address'},
        this.handleErrorAlert,
      );
      return;
    }

    if (!radius || radius < 150) {
      this.setState(
        {
          error:
            'Please specify an appropriate geofence radius. Radius must be greater than 150m',
        },
        this.handleErrorAlert,
      );
      return;
    }

    if (!loiteringDelay) {
      this.setState(
        {error: 'Please specify an appropriate geofence loitering delay'},
        this.handleErrorAlert,
      );
      return;
    }

    if (!notificationResponsiveness) {
      this.setState(
        {error: 'Please specify an appropriate geofence notification response'},
        this.handleErrorAlert,
      );
      return;
    }

    if (typeof expiration === 'string' && !validator.isNumeric(expiration)) {
      this.setState(
        {error: 'Please specify an appropriate geofence expiration time'},
        this.handleErrorAlert,
      );
      return;
    }

    if (!transitionTypes.length) {
      this.setState(
        {error: 'Please select atleast one geofence transition type'},
        this.handleErrorAlert,
      );
      return;
    }

    const configuration = {
      id,
      lat,
      lng,
      radius,
      loiteringDelay,
      notificationResponsiveness,
      registerOnDeviceRestart,
      expiration,
      transitionTypes,
      initialTriggerTransitionTypes,
    };

    addGeofence({configuration, address});
    navigation.goBack();
  };

  handleErrorAlert = () => {
    const {error} = this.state;
    Alert.alert(
      'Hmm, looks like you need to fix something',
      error,
      [
        {
          text: 'Got it',
          style: 'cancel',
        },
      ],
      {cancelable: false},
    );
  };

  handleInputChange = (type, text) => {
    this.setState({
      [type]: text,
    });
  };

  handleOnOkHiError = error => {
    console.log(error);
  };

  handleOnOkHiSuccess = location => {
    const {title, otherInformation, directions, geoPoint, id} = location;
    const {lat, lon} = geoPoint;
    this.setState({
      address: location,
      launchOkHi: false,
      lng: lon,
      subtitle: directions || otherInformation || null,
      id,
      lat,
      title,
    });
  };

  handleOnAddressSelect = () => {
    this.setState({launchOkHi: true});
  };

  hasTriggerType = (key, type) => {
    const items = this.state[key];
    return items.includes(type);
  };

  handleTransitionToggle = (key, type) => {
    let items = this.state[key];
    if (items.includes(type)) {
      items = items.filter(item => item !== type);
    } else {
      items.push(type);
    }
    this.setState({[key]: items});
  };

  renderAddress = () => {
    const {title, subtitle} = this.state;
    return (
      <AddressContainer>
        <TouchableOpacity onPress={this.handleOnAddressSelect}>
          <TextButton>Select an address</TextButton>
        </TouchableOpacity>
        {title ? <Text>{title}</Text> : null}
        {subtitle ? <Text small>{subtitle}</Text> : null}
      </AddressContainer>
    );
  };

  renderInputGroups = () => {
    const {radius, loiteringDelay, notificationResponsiveness} = this.state;
    return (
      <View>
        <InputGroup>
          <InputItem
            label="Radius* (m)"
            hint="Circular radius of the geofence in meters"
            placeholder="500"
            value={radius}
            onChangeText={text => this.handleInputChange('radius', text)}
            keyboardType="numeric"
          />
          <Separator />
          <InputItem
            label="Loitering Delay* (ms)"
            hint="Determines how long a user should be in a given radius before triggering the dwell event"
            placeholder="360000"
            value={loiteringDelay}
            onChangeText={text =>
              this.handleInputChange('loiteringDelay', text)
            }
            keyboardType="numeric"
          />
        </InputGroup>
        <InputGroup vertical>
          <InputItem
            label="Notification Responsiveness* (ms)"
            hint="Sets the best-effort notification responsiveness of the geofence"
            placeholder="5"
            value={notificationResponsiveness}
            onChangeText={text =>
              this.handleInputChange('notificationResponsiveness', text)
            }
            keyboardType="numeric"
          />
        </InputGroup>
      </View>
    );
  };

  renderSelectGeofenceTriggers = () => {
    const checkboxGroupContainerStyle = {
      flexDirection: 'row',
    };
    const checkboxStyle = {
      flexDirection: 'row',
      alignItems: 'center',
    };
    return (
      <View>
        <Text>Select geofence trigger types</Text>
        <View style={checkboxGroupContainerStyle}>
          <View style={checkboxStyle}>
            <CheckBox
              value={this.hasTriggerType('transitionTypes', 'enter')}
              onValueChange={() =>
                this.handleTransitionToggle('transitionTypes', 'enter')
              }
            />
            <Text>Enter</Text>
          </View>
          <View style={checkboxStyle}>
            <CheckBox
              value={this.hasTriggerType('transitionTypes', 'exit')}
              onValueChange={() =>
                this.handleTransitionToggle('transitionTypes', 'exit')
              }
            />
            <Text>Exit</Text>
          </View>
          <View style={checkboxStyle}>
            <CheckBox
              value={this.hasTriggerType('transitionTypes', 'dwell')}
              onValueChange={() =>
                this.handleTransitionToggle('transitionTypes', 'dwell')
              }
            />
            <Text>Dwell</Text>
          </View>
        </View>
      </View>
    );
  };

  renderInitialGeofenceTriggers = () => {
    const checkboxGroupContainerStyle = {
      flexDirection: 'row',
    };
    const checkboxStyle = {
      flexDirection: 'row',
      alignItems: 'center',
    };
    return (
      <View style={{marginTop: 15}}>
        <Text>Select initial geofence trigger types</Text>
        <View style={checkboxGroupContainerStyle}>
          <View style={checkboxStyle}>
            <CheckBox
              value={this.hasTriggerType(
                'initialTriggerTransitionTypes',
                'enter',
              )}
              onValueChange={() =>
                this.handleTransitionToggle(
                  'initialTriggerTransitionTypes',
                  'enter',
                )
              }
            />
            <Text>Enter</Text>
          </View>
          <View style={checkboxStyle}>
            <CheckBox
              value={this.hasTriggerType(
                'initialTriggerTransitionTypes',
                'exit',
              )}
              onValueChange={() =>
                this.handleTransitionToggle(
                  'initialTriggerTransitionTypes',
                  'exit',
                )
              }
            />
            <Text>Exit</Text>
          </View>
          <View style={checkboxStyle}>
            <CheckBox
              value={this.hasTriggerType(
                'initialTriggerTransitionTypes',
                'dwell',
              )}
              onValueChange={() =>
                this.handleTransitionToggle(
                  'initialTriggerTransitionTypes',
                  'dwell',
                )
              }
            />
            <Text>Dwell</Text>
          </View>
        </View>
      </View>
    );
  };

  renderShouldExpire = () => {
    const {expiration} = this.state;
    return (
      <View>
        <Text>Should expire?</Text>
        <CheckBox
          value={expiration !== -1}
          onValueChange={() =>
            this.setState({expiration: expiration !== -1 ? -1 : '3600'})
          }
        />
        {expiration !== -1 ? (
          <View>
            <InputItem
              label="Expiration (ms)"
              hint="Time in milliseconds that the geofence should stay active. 1m = 60000ms"
              placeholder="500"
              value={expiration}
              onChangeText={text => this.handleInputChange('expiration', text)}
            />
          </View>
        ) : null}
      </View>
    );
  };

  renderRegisterOnDeviceRestart = () => {
    const {registerOnDeviceRestart} = this.state;
    return (
      <View>
        <Text>Register on device restart?</Text>
        <CheckBox
          value={registerOnDeviceRestart}
          onValueChange={() =>
            this.setState({registerOnDeviceRestart: !registerOnDeviceRestart})
          }
        />
      </View>
    );
  };

  render() {
    const {user} = this.props.context;
    const {launchOkHi} = this.state;
    return (
      <Container>
        <OkHiLocationManager
          core={core}
          user={user}
          launch={launchOkHi}
          onSuccess={this.handleOnOkHiSuccess}
          onError={this.handleOnOkHiError}
          onCloseRequest={() => this.setState({launchOkHi: false})}
          loader={<FullScreenLoader />}
        />
        <Container>
          <ScrollView>
            <KeyBoardAvoidView behavior="padding">
              {this.renderAddress()}
              <HR />
              {this.renderInputGroups()}
              <View style={{marginTop: 15, marginHorizontal: 15}}>
                {this.renderRegisterOnDeviceRestart()}
              </View>
              <View style={{marginTop: 15, marginHorizontal: 15}}>
                {this.renderShouldExpire()}
              </View>
              <View style={{marginTop: 15, marginHorizontal: 15}}>
                {this.renderSelectGeofenceTriggers()}
                {this.renderInitialGeofenceTriggers()}
              </View>
            </KeyBoardAvoidView>
          </ScrollView>
        </Container>
        <FullButton color="#00796B" label="SAVE" onPress={this.handleSubmit} />
      </Container>
    );
  }
}

const HR = styled.View`
  border: 1px solid rgba(66, 66, 66, 0.2);
  margin: 15px;
  margin-bottom: 0;
`;

const KeyBoardAvoidView = styled.KeyboardAvoidingView`
  flex: 1;
`;

const Container = styled.View`
  flex: 1;
`;

const AddressContainer = styled.View`
  margin: 10px 0 0 15px;
`;

const TextButton = styled.Text`
  color: #00838f;
  margin-bottom: 10px;
  font-size: ${props => (props.small ? '12px' : '14px')};
`;

const Text = styled.Text`
  font-size: ${props => (props.small ? '12px' : '14px')};
  opacity: ${props => (props.small ? 0.8 : 1)};
`;

const InputGroup = styled.View`
  flex-direction: ${props => (props.vertical ? 'column' : 'row')};
  padding: 0 15px;
  margin-top: 15px;
`;

const Separator = styled.View`
  margin: 0 5px;
`;

export default withContext(AddGeofenceScreen);
