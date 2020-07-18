import React from 'react';
import {CommonActions} from '@react-navigation/native';
import FullScreenLoader from '../components/FullScreenLoader';
import {wait} from '../services/Utils';
import {withContext} from '../context';

class InitScreen extends React.Component {
  shouldComponentUpdate() {
    return false;
  }

  async componentDidMount() {
    const {context} = this.props;
    const {user} = await context.hydrate();
    await wait(1500);
    if (user) {
      this.handleNavigation('Home');
    } else {
      this.handleNavigation('Login');
    }
  }

  handleNavigation = route => {
    const {navigation} = this.props;
    navigation.dispatch(
      CommonActions.reset({
        index: 0,
        routes: [
          {
            name: route,
          },
        ],
      }),
    );
  };

  render() {
    return <FullScreenLoader />;
  }
}

export default withContext(InitScreen);
