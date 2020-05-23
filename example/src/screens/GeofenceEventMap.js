import React from 'react';
import styled from 'styled-components';
import MapView, {PROVIDER_GOOGLE, Marker, Circle} from 'react-native-maps';

const FAR_ZOOM_LEVEL = {
  latitudeDelta: 0.02829186345218493,
  longitudeDelta: 0.01591019332408905,
};

export default function GeofenceEventMapScreen({route: {params}, navigation}) {
  const {
    geofenceEvent,
    geofence: {configuration, address},
  } = params;
  const geofenceAddress = {
    latitude: configuration.lat,
    longitude: configuration.lng,
  };
  const region = {
    ...geofenceAddress,
    ...FAR_ZOOM_LEVEL,
  };
  const triggerCoord = {
    latitude: geofenceEvent.lat,
    longitude: geofenceEvent.lng,
  };
  const mapStyles = {flex: 1};
  return (
    <Container>
      <MapView provider={PROVIDER_GOOGLE} style={mapStyles} region={region}>
        <Marker
          title={address.title || 'Geofence Address'}
          description="Where the geofence was created"
          pinColor="#1565C0"
          coordinate={triggerCoord}
        />
        <Marker
          title="Triggering location"
          description="Where the geofence event was triggered"
          pinColor="#b71c1c"
          coordinate={triggerCoord}
        />
        <Circle
          center={geofenceAddress}
          radius={configuration.radius}
          strokeWidth={2}
          strokeColor="rgba(21,101,192, 0.5)"
          fillColor="rgba(21,101,192, 0.2)"
        />
        <Circle
          center={triggerCoord}
          radius={geofenceEvent.accuracy}
          strokeWidth={2}
          strokeColor="rgba(183,28,28, 0.5)"
          fillColor="rgba(183,28,28, 0.2)"
        />
      </MapView>
    </Container>
  );
}

const Container = styled.View`
  flex: 1;
`;
