import React from 'react';
import {ToastAndroid} from 'react-native';
import Repository from '../services/Repository';
import {createGeofenceEvent, sendGeofenceEventReview} from '../services/Utils';
export const Context = React.createContext();

export const withContext = Component => {
  return props => {
    return (
      <Context.Consumer>
        {context => <Component context={context} {...props} />}
      </Context.Consumer>
    );
  };
};

export class Provider extends React.Component {
  repo = new Repository();

  state = {
    user: null,
    geofences: [],
    events: [],
  };

  hydrate = async () => {
    try {
      const user = await this.repo.getUser();
      const geofences = await this.repo.getGeofences();
      const events = await this.repo.getGeofenceEvents();
      this.setState({user, geofences, events});
      return {user, geofences, events};
    } catch (error) {
      console.log(error);
    }
  };

  submitGeofenceEventReview = async (geofenceEvents = [], review) => {
    let {events, geofences} = this.state;
    const originalEvents = [...events];
    const uuids = geofenceEvents.map(geofenceEvent => geofenceEvent.uuid);
    const geofenceId = geofenceEvents[0].id;
    const geofence = geofences.find(
      ({configuration}) => configuration.id === geofenceId,
    );
    events = events.filter(e => !uuids.includes(e.uuid));
    this.setState({events});
    try {
      let avds = geofenceEvents.map(async geofenceEvent => {
        const {avd_ids} = await createGeofenceEvent(geofenceEvent);
        const [avdId] = avd_ids;
        return {geofenceEvent, avdId};
      });
      avds = await Promise.all(avds);
      const submissions = avds.map(({avdId, geofenceEvent}) => {
        return sendGeofenceEventReview(
          avdId,
          review,
          geofence.configuration,
          geofenceEvent,
        );
      });
      const removal = uuids.map(uuid => this.repo.removeGeofenceEvent(uuid));
      await Promise.all(submissions);
      await Promise.all(removal);
      ToastAndroid.show(
        'Your review has been submitted successfully!',
        ToastAndroid.SHORT,
      );
      console.log('reviews submitted sucessfully..');
    } catch (error) {
      console.log(error);
      this.setState({events: originalEvents});
      if (error.response) {
        ToastAndroid.show(
          `Error ${error.response.status ||
            ''}- Could not submit your review, please try again`,
          ToastAndroid.LONG,
        );
      } else {
        ToastAndroid.show(
          'Error - Could not reach OkHi servers, please check your internet connection and  try again',
          ToastAndroid.LONG,
        );
      }
    }
  };

  putGeofenceEvents = (events = []) => {
    this.setState({events});
  };

  putUser = async user => {
    try {
      this.setState({user});
      await this.repo.addUser(user);
    } catch (error) {
      throw error;
    }
  };

  putGeofences = async (geofences = []) => {
    this.setState({geofences});
  };

  addGeofence = async geofence => {
    try {
      const geofences = [geofence, ...this.state.geofences];
      this.setState({geofences});
      await this.repo.addGeofence(geofence);
    } catch (error) {
      throw error;
    }
  };

  render() {
    const {
      putUser,
      addGeofence,
      putGeofences,
      putGeofenceEvents,
      submitGeofenceEventReview,
      hydrate,
    } = this;
    return (
      <Context.Provider
        value={{
          ...this.state,
          putUser,
          addGeofence,
          putGeofences,
          putGeofenceEvents,
          submitGeofenceEventReview,
          hydrate,
        }}>
        {this.props.children}
      </Context.Provider>
    );
  }
}
