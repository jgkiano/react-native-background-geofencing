import React from 'react';
import {Text} from 'react-native';
import styled from 'styled-components';
import FullButton from '../components/FullButton';
import HomeEmptyState from '../components/HomeEmptyState';
import HomeGeofenceList from '../components/HomeGeofenceList';

import {withContext} from '../context';

class HomeScreen extends React.Component {
  handleOnPress = () => {
    const {navigation} = this.props;
    navigation.navigate('AddGeofence');
  };

  handleOnGeofenceSelect = geofence => {
    const {navigation, context} = this.props;
    const {events} = context;
    const geofenceEvents = events.filter(
      ({id}) => geofence.configuration.id === id,
    );
    navigation.navigate('GeofenceHistory', {geofence, events: geofenceEvents});
  };

  renderPage = () => {
    const {context} = this.props;
    const {geofences, events} = context;
    if (!geofences.length) {
      return <HomeEmptyState />;
    }
    return (
      <HomeGeofenceList
        geofences={geofences}
        onGeofenceSelect={this.handleOnGeofenceSelect}
        events={events}
      />
    );
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
