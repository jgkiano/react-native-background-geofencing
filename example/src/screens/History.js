/* eslint-disable react-native/no-inline-styles */
import React from 'react';
import {View, Text} from 'react-native';
import {CommonActions} from '@react-navigation/native';
import moment from 'moment';
import Repository from '../services/Repository';
import FullScreenLoader from '../components/FullScreenLoader';
import {FlatList} from 'react-native-gesture-handler';
import FullButton from '../components/FullButton';

export default class History extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      events: [],
    };
    const {geofenceId} = props.route.params;
    this.geofenceId = geofenceId;
    this.repo = new Repository();
    this.timer = setTimeout(async () => {
      const events = await this.repo.getGeofenceEvents(geofenceId);
      this.setState({events, loading: false});
      clearTimeout(this.timer);
    }, 1000);
  }

  renderItem = ({item}) => {
    const {data} = item;
    return (
      <View
        style={{
          backgroundColor: 'white',
          marginHorizontal: 15,
          padding: 15,
          elevation: 2,
          borderRadius: 8,
        }}>
        <Text>{item.event}</Text>
        {item.event === 'GEOFENCE_TRANSITION_UNKNOWN' ||
        item.event === 'GEOFENCE_TRANSITION_ERROR' ? (
          <View style={{marginTop: 30}}>
            <Text>{JSON.stringify(data)}</Text>
          </View>
        ) : (
          <View style={{marginTop: 30}}>
            <Text style={{fontSize: 12, opacity: 0.7}}>{`Triggering Geopoint: ${
              data.lat
            },${data.lng}`}</Text>
            <Text style={{fontSize: 12, opacity: 0.7}}>{`Accuracy: ${
              data.accuracy
            }`}</Text>
            <Text style={{fontSize: 12, opacity: 0.7}}>{`GPS Provider: ${
              data.provider
            }`}</Text>
            <Text style={{fontSize: 12, opacity: 0.7}}>{`Event time: ${moment(
              data.time,
            ).format('h:mm:ss a, dddd, MMMM Do YYYY')}`}</Text>
          </View>
        )}
      </View>
    );
  };

  renderHistoryList = () => {
    const {events} = this.state;
    if (!events.length) {
      return (
        <View style={{flexDirection: 'row', alignItems: 'center', flex: 1}}>
          <Text style={{fontSize: 16, fontWeight: '600', opacity: 0.3}}>
            Nothing to show yet ಥ_ಥ
          </Text>
        </View>
      );
    }
    return (
      <FlatList
        style={{paddingVertical: 15, flex: 1, width: '100%'}}
        data={events}
        renderItem={this.renderItem}
        keyExtractor={(item, index) => String(index)}
        ItemSeparatorComponent={() => <View style={{padding: 5}} />}
      />
    );
  };

  renderPage = () => {
    const {loading} = this.state;
    if (loading) {
      return <FullScreenLoader />;
    } else {
      return this.renderHistoryList();
    }
  };

  render() {
    return (
      <View
        style={{
          flex: 1,
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
        }}>
        {this.renderPage()}
        <View style={{width: '100%'}}>
          <FullButton
            label="Remove Geofence"
            color="#b71c1c"
            onPress={() => {
              this.setState({loading: true}, async () => {
                await this.repo.removeGeofence(this.geofenceId);
                this.props.navigation.dispatch(
                  CommonActions.reset({
                    index: 0,
                    routes: [{name: 'Geofences'}],
                  }),
                );
              });
            }}
          />
        </View>
      </View>
    );
  }
}
