import React from 'react';
import Repository from '../services/Repository';
import {createGeofenceEvent} from '../services/Utils';
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

  submitGeofenceEventReview = async (uuid, review) => {
    let {events} = this.state;
    const originalEvents = [...events];
    try {
      const [event] = events.filter(e => e.uuid === uuid);
      events = events.filter(e => e.uuid !== uuid);
      this.setState({events});
      const response = await createGeofenceEvent(event);
      console.log(response);
    } catch (error) {
      console.log(error);
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
        }}>
        {this.props.children}
      </Context.Provider>
    );
  }
}
