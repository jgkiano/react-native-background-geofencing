import React from 'react';
import {Text} from 'react-native';
import styled from 'styled-components';
import FullButton from '../components/FullButton';
import HomeEmptyState from '../components/HomeEmptyState';
import {withContext} from '../context';

class HomeScreen extends React.Component {
  handleOnPress = () => {
    const {navigation} = this.props;
    navigation.navigate('AddGeofence');
  };

  renderPage = () => {
    const {context} = this.props;
    const {geofences} = context;
    if (!geofences.length) {
      return <HomeEmptyState />;
    }
    return <Text>We have some geofences in state, woop!</Text>;
  };

  render() {
    return (
      <Container>
        {this.renderPage()}
        <FullButton label="ADD A GEOFENCE" onPress={this.handleOnPress} />
      </Container>
    );
  }
}

const Container = styled.View`
  flex: 1;
  flex-direction: column;
`;

export default withContext(HomeScreen);
