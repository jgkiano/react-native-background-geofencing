/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {View, Modal} from 'react-native';
import Repository from '../services/Repository';
import FullScreenLoader from '../components/FullScreenLoader';
import FullButton from '../components/FullButton';
import HomeGeofences from '../components/HomeGeofences';
import HomeEmptyState from '../components/HomeEmptyState';
import ModalBody from '../components/ModalBody';

export default class Home extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      geofences: [],
      modalVisible: false,
    };
    this.repo = new Repository();
    this.timer = setTimeout(async () => {
      const geofences = await this.repo.getGeofences();
      this.setState({geofences, loading: false});
      clearTimeout(this.timer);
    }, 1500);
  }

  handleFormSubmit = geofence => {
    this.setState({modalVisible: false, loading: true}, async () => {
      const geofences = await this.repo.addGeofence(geofence);
      this.setState({geofences, loading: false});
    });
  };

  handleItemTap = geofenceId => {
    this.props.navigation.push('History', {geofenceId});
  };

  renderPage = () => {
    const {loading, geofences} = this.state;
    if (loading) {
      return <FullScreenLoader />;
    }
    if (geofences && geofences.length) {
      return (
        <HomeGeofences geofences={geofences} onItemTap={this.handleItemTap} />
      );
    }
    return <HomeEmptyState />;
  };

  render() {
    const {loading, modalVisible} = this.state;
    return (
      <View style={{flex: 1, flexDirection: 'column'}}>
        <Modal
          animationType="slide"
          transparent={true}
          visible={modalVisible}
          onRequestClose={() => {
            this.setState({modalVisible: false});
          }}>
          <ModalBody onSubmit={this.handleFormSubmit} />
        </Modal>
        {this.renderPage()}
        <FullButton
          label="ADD A GEOFENCE"
          disabled={loading}
          onPress={() => this.setState({modalVisible: true})}
        />
      </View>
    );
  }
}
