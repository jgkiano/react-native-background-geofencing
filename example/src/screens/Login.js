/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {View, SafeAreaView, ScrollView} from 'react-native';
import styled from 'styled-components/native';
import {CommonActions} from '@react-navigation/native';
import InputItem from '../components/InputItem';
import FullButton from '../components/FullButton';
import ErrorBox from '../components/ErrorBox';
import manifest from '../../package.json';
import {withContext} from '../context';

class LoginScreen extends React.Component {
  state = {
    firstName: '',
    lastName: '',
    phone: '',
    errors: [],
  };

  handleNavigation = () => {
    const {firstName, lastName, phone} = this.state;
    const {navigation, context} = this.props;
    const user = {firstName, lastName, phone};
    context.putUser(user);
    navigation.dispatch(
      CommonActions.reset({
        index: 0,
        routes: [
          {
            name: 'Home',
            params: {
              user,
            },
          },
        ],
      }),
    );
  };

  handleOnPress = () => {
    const errors = [];
    const {firstName, lastName, phone} = this.state;
    if (!firstName.length) {
      errors.push('Please provide your first name');
    }
    if (!lastName.length) {
      errors.push('Please provide your last name');
    }
    if (phone.length < 6 || !phone.startsWith('+')) {
      errors.push(
        'Please provide your phone number with the country code prefix e.g +254700110590',
      );
    }
    if (errors.length) {
      this.setState({errors});
    } else {
      this.handleNavigation();
    }
  };

  renderErrors = () => {
    const {errors} = this.state;
    if (!errors.length) {
      return null;
    }
    return <ErrorBox errors={errors} />;
  };

  render() {
    const {firstName, lastName, phone} = this.state;
    return (
      <SafeAreaView>
        <ScrollView>
          <Form>
            <View>
              <InputItem
                label="First name*"
                placeholder="Julius"
                onChangeText={text => this.setState({firstName: text})}
                value={firstName}
                autoCapitalize="words"
                autoCorrect={false}
              />
            </View>
            <View>
              <InputItem
                label="Last name*"
                placeholder="Kiano"
                onChangeText={text => this.setState({lastName: text})}
                value={lastName}
                autoCapitalize="words"
                autoCorrect={false}
              />
            </View>
            <View>
              <InputItem
                label="Phone*"
                placeholder="+254700110590"
                onChangeText={text => this.setState({phone: text})}
                value={phone}
                keyboardType="phone-pad"
                autoCorrect={false}
              />
            </View>
            <View>
              <FullButton
                label="Login"
                style={{borderRadius: 8}}
                onPress={this.handleOnPress}
              />
            </View>
          </Form>
          {this.renderErrors()}
          <SmallText>App version: {manifest.version}</SmallText>
        </ScrollView>
      </SafeAreaView>
    );
  }
}

const Form = styled.View`
  margin: 15px;
`;

const SmallText = styled.Text`
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-top: 15px;
  opacity: 0.7;
  color: ${props => props.color || '#424242'};
`;

export default withContext(LoginScreen);
