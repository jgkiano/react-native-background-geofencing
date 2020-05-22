import React from 'react';
import {FlatList, TouchableOpacity} from 'react-native';
import styled from 'styled-components';
import moment from 'moment';
import {withContext} from '../context';
import ReviewModal from '../components/ReviewModal';

class GeofenceHistoryScreen extends React.Component {
  state = {
    selectedEvent: null,
  };

  handleOnReviewSubmit = review => {
    const {
      selectedEvent: {uuid},
    } = this.state;
    const {submitGeofenceEventReview} = this.props.context;
    submitGeofenceEventReview(uuid, review);
    this.setState({selectedEvent: null});
  };

  handleOnReviewButtonPress = ({uuid}) => {
    const [selectedEvent] = this.props.events.filter(
      event => event.uuid === uuid,
    );
    this.setState({selectedEvent});
  };

  handleOnMapButtonPress = geofenceEvent => {
    const {navigation, geofence} = this.props;
    navigation.navigate('GeofenceEventMap', {geofenceEvent, geofence});
  };

  renderItem = geofenceEvent => {
    const {accuracy, time, event, uuid} = geofenceEvent;
    const timeText = moment(time).format('[At] h:mm a [on] dddd, MMMM Do YYYY');
    return (
      <ItemContainer>
        <ItemSection>
          <Title>{event}</Title>
          <Subtitle>{timeText}</Subtitle>
          <Subtitle>GPS Accuracy: {Math.round(accuracy)}</Subtitle>
        </ItemSection>
        <ItemSection buttonBar>
          <TouchableOpacity
            onPress={() => this.handleOnReviewButtonPress(geofenceEvent)}>
            <ButtonText>REVIEW</ButtonText>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => this.handleOnMapButtonPress(geofenceEvent)}>
            <ButtonText>MAP</ButtonText>
          </TouchableOpacity>
        </ItemSection>
      </ItemContainer>
    );
  };

  renderList = () => {
    return (
      <FlatList
        data={this.props.events}
        extraData={this.props.events}
        renderItem={({item}) => this.renderItem(item)}
        keyExtractor={({uuid}) => uuid}
        ItemSeparatorComponent={Separator}
      />
    );
  };

  render() {
    const {selectedEvent} = this.state;
    return (
      <Container>
        {selectedEvent ? (
          <ReviewModal
            onSubmit={this.handleOnReviewSubmit}
            onCloseRequest={() => this.setState({selectedEvent: null})}
          />
        ) : null}
        {this.renderList()}
      </Container>
    );
  }
}

const Separator = styled.View`
  border-bottom-width: 1px;
  border-color: #e0e0e0;
`;

const ButtonText = styled.Text`
  padding: 0 15px;
  padding-top: 10px;
  color: #0d47a1;
  font-weight: bold;
  opacity: 0.8;
`;

const ItemContainer = styled.View`
  background-color: white;
  padding: 15px;
`;

const ItemSection = styled.View`
  flex-direction: ${props => (props.buttonBar ? 'row' : 'column')};
  justify-content: ${props => (props.buttonBar ? 'flex-end' : 'flex-start')};
`;

const Title = styled.Text`
  font-size: 18px;
  font-weight: bold;
  opacity: 0.7;
  line-height: 34px;
  text-transform: capitalize;
`;

const Subtitle = styled.Text`
  opacity: 0.8;
`;

const Container = styled.View`
  flex: 1;
`;

function GeofenceHistoryHOC(props) {
  const {
    navigation,
    route: {params},
    context,
  } = props;
  const {geofence} = params;
  const {address, configuration} = geofence;
  let title = address.title || 'Geofence History';
  let {events} = context;
  events = events.filter(event => event.id === configuration.id);
  title = title.length > 22 ? `${title.substring(0, 22)}...` : title;
  navigation.setOptions({title});
  return (
    <GeofenceHistoryScreen {...props} events={events} geofence={geofence} />
  );
}

export default withContext(GeofenceHistoryHOC);
