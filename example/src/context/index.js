import React from 'react';
import Repository from '../services/Repository';

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
    const {putUser, addGeofence, putGeofences, putGeofenceEvents} = this;
    return (
      <Context.Provider
        value={{
          ...this.state,
          putUser,
          addGeofence,
          putGeofences,
          putGeofenceEvents,
        }}>
        {this.props.children}
      </Context.Provider>
    );
  }
}
