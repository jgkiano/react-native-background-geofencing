import React from 'react';
import {
  FlatList,
  TouchableOpacity,
  TouchableNativeFeedback,
  Platform,
} from 'react-native';
import styled from 'styled-components';
import moment from 'moment';

const HomeGeofenceList = ({
  geofences = [],
  events = [],
  onGeofenceSelect = () => {},
} = {}) => {
  const renderItem = geofence => {
    const Touchable = Platform.select({
      android: TouchableNativeFeedback,
      ios: TouchableOpacity,
    });
    const {configuration, address} = geofence;
    const {createdAt} = configuration;
    const {title, otherInformation, directions} = address;
    const subtitle = directions || otherInformation || null;
    const time = moment(createdAt).format(
      '[Created on] Do MMMM YYYY [at] h:mm a',
    );
    return (
      <Touchable onPress={() => onGeofenceSelect(geofence)}>
        <ItemContainer>
          <NotificationBanner />
          <TitleContainer>
            <Title>{title}</Title>
            {subtitle ? <Subtitle>{subtitle}</Subtitle> : null}
            <Subtitle>{time}</Subtitle>
          </TitleContainer>
        </ItemContainer>
      </Touchable>
    );
  };
  const renderList = () => {
    return (
      <FlatList
        data={geofences}
        renderItem={({item}) => renderItem(item)}
        keyExtractor={({configuration}) => configuration.id}
      />
    );
  };
  return <Container>{renderList()}</Container>;
};

const Container = styled.View`
  flex: 1;
`;

const ItemContainer = styled.View`
  padding: 15px;
  background-color: white;
  flex-direction: row;
  border-bottom-width: 1px;
  border-bottom-color: #e0e0e0;
  border-left-width: 6px;
  border-left-color: ${props => (props.active ? '#00838f' : '#BDBDBD')};
`;

const NotificationBanner = styled.View`
  height: 100%;

  color: green;
`;

const TitleContainer = styled.View``;

const Title = styled.Text`
  font-size: 16px;
  line-height: 34px;
`;

const Subtitle = styled.Text`
  opacity: 0.7;
  font-size: 12px;
`;

const NotificationText = styled.Text`
  font-size: 16px;
  margin-top: 8px;
  color: #00838f;
`;

export default HomeGeofenceList;
