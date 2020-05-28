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

  submitGeofenceEventReview = async (uuid, review) => {
    let {events, geofences} = this.state;
    const originalEvents = [...events];
    try {
      const [event] = events.filter(e => e.uuid === uuid);
      const [geofence] = geofences.filter(
        ({configuration}) => configuration.id === event.id,
      );
      events = events.filter(e => e.uuid !== uuid);
      this.setState({events});
      const {avd_ids} = await createGeofenceEvent(event);
      const [avdId] = avd_ids;
      await sendGeofenceEventReview(
        avdId,
        review,
        geofence.configuration,
        event,
      );
      await this.repo.removeGeofenceEvent(uuid);
      ToastAndroid.show(
        'Your review has been submitted successfully!',
        ToastAndroid.SHORT,
      );
    } catch (error) {
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
