import React from 'react';
import styled from 'styled-components';
import Repository from '../services/Repository';
import FullButton from '../components/FullButton';
import HomeEmptyState from '../components/HomeEmptyState';

export default class HomeScreen extends React.Component {
  repo = new Repository();

  handleOnPress = () => {
    const {navigation, route} = this.props;
    navigation.navigate('AddGeofence', {user: route.params.user});
  };

  renderPage = () => {
    return <HomeEmptyState />;
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
