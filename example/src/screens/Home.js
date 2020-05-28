import React from 'react';
import styled from 'styled-components';
import FullButton from '../components/FullButton';
import HomeEmptyState from '../components/HomeEmptyState';
import HomeGeofenceList from '../components/HomeGeofenceList';

import {withContext} from '../context';

class HomeScreen extends React.Component {
  state = {
    refreshing: false,
  };

  handleOnPress = () => {
    const {navigation} = this.props;
    navigation.navigate('AddGeofence');
  };

  handleOnGeofenceSelect = geofence => {
    const {navigation} = this.props;
    navigation.navigate('GeofenceHistory', {geofence});
  };

  handleOnRefresh = () => {
    const {context} = this.props;
    this.setState({refreshing: true}, async () => {
      await context.hydrate();
      this.setState({refreshing: false});
    });
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
        refreshing={this.state.refreshing}
        onRefresh={this.handleOnRefresh}
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
