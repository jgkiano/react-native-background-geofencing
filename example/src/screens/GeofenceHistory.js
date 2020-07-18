import React from 'react';
import {
  FlatList,
  TouchableOpacity,
  TouchableNativeFeedback,
  Platform,
  View,
  Vibration,
  BackHandler,
} from 'react-native';
import styled from 'styled-components';
import moment from 'moment';
import CheckBox from '@react-native-community/checkbox';
import {withContext} from '../context';
import ReviewModal from '../components/ReviewModal';

class GeofenceHistoryScreen extends React.Component {
  state = {
    multiSelect: false,
    modalVisible: false,
    selectedEvents: [],
  };

  componentDidMount() {
    this.setUpBackHandler();
  }

  componentWillUnmount() {
    BackHandler.removeEventListener(
      'hardwareBackPress',
      this.handleOnHardwareBackPress,
    );
  }

  setUpBackHandler = () => {
    BackHandler.addEventListener(
      'hardwareBackPress',
      this.handleOnHardwareBackPress,
    );
  };

  handleOnHardwareBackPress = () => {
    const {multiSelect} = this.state;
    if (multiSelect) {
      this.setState({multiSelect: false, selectedEvents: []});
      return true;
    } else {
      return false;
    }
  };

  handleOnReviewSubmit = review => {
    const {selectedEvents} = this.state;
    const {submitGeofenceEventReview} = this.props.context;
    submitGeofenceEventReview(selectedEvents, review);
    this.setState({
      modalVisible: false,
      multiSelect: false,
      selectedEvents: [],
    });
  };

  handleOnReviewButtonPress = geofenceEvent => {
    this.setState({
      modalVisible: true,
      selectedEvents: [geofenceEvent],
    });
  };

  handleOnMapButtonPress = geofenceEvent => {
    const {navigation, geofence} = this.props;
    navigation.navigate('GeofenceEventMap', {geofenceEvent, geofence});
  };

  handleOnLongPress = geofenceEvent => {
    const {multiSelect} = this.state;
    if (!multiSelect) {
      Vibration.vibrate(70);
      this.setState({multiSelect: true, selectedEvents: [geofenceEvent]});
    }
  };

  handleItemPress = ({uuid}) => {
    let {selectedEvents} = this.state;
    const {multiSelect} = this.state;
    if (multiSelect) {
      const existingSelection = selectedEvents.find(
        selectedEvent => selectedEvent.uuid === uuid,
      );
      if (existingSelection) {
        selectedEvents = selectedEvents.filter(
          selectedEvent => selectedEvent.uuid !== uuid,
        );
      } else {
        const selectedEvent = this.props.events.find(
          event => event.uuid === uuid,
        );
        selectedEvents = [...selectedEvents, selectedEvent];
      }
      this.setState({
        selectedEvents,
        multiSelect: selectedEvents.length ? true : false,
      });
    }
  };

  handleOnToolBarSelectAllPress = () => {
    const selectedEvents = [...this.props.events];
    this.setState({selectedEvents});
  };

  handleOnTollBarReview = () => {
    const {selectedEvents} = this.state;
    if (selectedEvents.length) {
      this.setState({modalVisible: true});
    }
  };

  renderItem = geofenceEvent => {
    const {multiSelect, selectedEvents} = this.state;
    const checkBoxContainerStyle = {paddingRight: 15};
    const rightSectionContainerStyle = {flex: 1};
    const {accuracy, time, event, uuid} = geofenceEvent;
    const timeText = moment(time).format('[At] h:mm a [on] dddd, MMMM Do YYYY');
    const Touchable = Platform.select({
      ios: TouchableOpacity,
      android: TouchableNativeFeedback,
    });
    const isSelected = selectedEvents.find(
      selectedEvent => selectedEvent.uuid === uuid,
    )
      ? true
      : false;
    return (
      <Touchable
        onLongPress={() => this.handleOnLongPress(geofenceEvent)}
        onPress={() => this.handleItemPress(geofenceEvent)}>
        <ItemContainer>
          {multiSelect ? (
            <View style={checkBoxContainerStyle}>
              <CheckBox
                value={isSelected}
                onValueChange={() => this.handleItemPress(geofenceEvent)}
              />
            </View>
          ) : null}
          <View style={rightSectionContainerStyle}>
            <ItemSection>
              <Title>{event}</Title>
              <Subtitle>{timeText}</Subtitle>
              <Subtitle>GPS Accuracy: {Math.round(accuracy)}</Subtitle>
            </ItemSection>
            {multiSelect ? (
              <ItemSection buttonBar disabled={multiSelect} />
            ) : (
              <ItemSection buttonBar disabled={multiSelect}>
                <TouchableOpacity
                  onPress={() => this.handleOnReviewButtonPress(geofenceEvent)}>
                  <ButtonText>REVIEW</ButtonText>
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={() => this.handleOnMapButtonPress(geofenceEvent)}>
                  <ButtonText>MAP</ButtonText>
                </TouchableOpacity>
              </ItemSection>
            )}
          </View>
        </ItemContainer>
      </Touchable>
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

  renderMultiSelectToolBar = () => {
    const {multiSelect, selectedEvents} = this.state;
    if (!multiSelect) {
      return null;
    }
    return (
      <ToolBarContainer>
        <ToolBarRightContainer>
          <ToolBarTitleText>{selectedEvents.length} selected</ToolBarTitleText>
        </ToolBarRightContainer>
        <ToolBarRight>
          <ToolBarButton
            onPress={() =>
              this.setState({multiSelect: false, selectedEvents: []})
            }>
            <ToolBarText>Clear</ToolBarText>
          </ToolBarButton>
          <ToolBarButton onPress={this.handleOnToolBarSelectAllPress}>
            <ToolBarText>Select All</ToolBarText>
          </ToolBarButton>
          <ToolBarButton onPress={this.handleOnTollBarReview} color="#00838f">
            <ToolBarText>Review</ToolBarText>
          </ToolBarButton>
        </ToolBarRight>
      </ToolBarContainer>
    );
  };

  render() {
    const {modalVisible} = this.state;
    return (
      <Container>
        {this.renderMultiSelectToolBar()}
        {modalVisible ? (
          <ReviewModal
            onSubmit={this.handleOnReviewSubmit}
            onCloseRequest={() =>
              this.setState({modalVisible: false, selectedEvents: []})
            }
          />
        ) : null}
        {this.renderList()}
      </Container>
    );
  }
}

const ToolBarContainer = styled.View`
  padding: 15px;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  border-bottom-width: 2px;
  border-color: #e0e0e0;
`;

const ToolBarRightContainer = styled.View`
  flex: 0.5;
`;

const ToolBarRight = styled.View`
  flex-direction: row;
  flex: 1;
  justify-content: space-between;
  align-items: center;
`;

const ToolBarTitleText = styled.Text`
  font-size: 16px;
  font-weight: bold;
  opacity: 0.8;
`;

const ToolBarButton = styled.TouchableOpacity`
  background-color: ${props => props.color || '#1565c0'};
  border-radius: 60px;
  padding: 3px 15px;
`;

const ToolBarText = styled.Text`
  color: white;
`;

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
  flex-direction: row;
  align-items: center;
`;

const ItemSection = styled.View`
  flex-direction: ${props => (props.buttonBar ? 'row' : 'column')};
  justify-content: ${props => (props.buttonBar ? 'flex-end' : 'flex-start')};
  padding-bottom: ${props => (props.disabled ? '30px' : '0')};
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
