import React from 'react';
import {FlatList} from 'react-native';
import styled from 'styled-components';
import FontAwesome5 from 'react-native-vector-icons/FontAwesome5';

class GeofenceHistory extends React.Component {
  events = [];

  constructor(props) {
    super(props);
    const {
      navigation,
      route: {params},
    } = props;
    const {geofence, events} = params;
    const {address} = geofence;
    let title = address.title || 'Geofence History';
    title = title.length > 22 ? `${title.substring(0, 22)}...` : title;
    navigation.setOptions({title});
    this.events = events;
  }

  renderItem = () => {
    return null;
  };
  renderList = () => {
    console.log(this.events);
    return (
      <FlatList
        data={this.events}
        renderItem={({item}) => this.renderItem(item)}
        keyExtractor={({id}) => id}
      />
    );
  };

  render() {
    return <Container>{this.renderList()}</Container>;
  }
}

const Container = styled.View`
  flex: 1;
`;

export default GeofenceHistory;
