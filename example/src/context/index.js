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
  };

  putUser = async user => {
    try {
      this.setState({user});
      await this.repo.addUser(user);
    } catch (error) {
      throw error;
    }
  };

  render() {
    const {putUser} = this;
    return (
      <Context.Provider value={{...this.state, putUser}}>
        {this.props.children}
      </Context.Provider>
    );
  }
}
