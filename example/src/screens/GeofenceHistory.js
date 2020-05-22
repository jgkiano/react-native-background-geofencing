import React from 'react';
import {Text} from 'react-native';

class GeofenceHistory extends React.Component {
  constructor(props) {
    super(props);
    const {
      navigation,
      route: {params},
    } = props;
    const {geofence} = params;
    const {address} = geofence;
    let title = address.title || 'Geofence History';
    title = title.length > 22 ? `${title.substring(0, 22)}...` : title;
    navigation.setOptions({title});
  }
  render() {
    return <Text>History me this!</Text>;
  }
}

export default GeofenceHistory;
