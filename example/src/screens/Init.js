import React from 'react';
import {CommonActions} from '@react-navigation/native';
import FullScreenLoader from '../components/FullScreenLoader';
import Repository from '../services/Repository';
import {wait} from '../services/Utils';

export default class InitScreen extends React.Component {
  repo = new Repository();

  async componentDidMount() {
    const {navigation} = this.props;
    const user = await this.repo.getUser();
    await wait(2000);
    if (user) {
      navigation.dispatch(
        CommonActions.reset({
          index: 0,
          routes: [
            {
              name: 'Home',
              params: {
                user,
              },
            },
          ],
        }),
      );
    } else {
      navigation.dispatch(
        CommonActions.reset({
          index: 0,
          routes: [
            {
              name: 'Login',
              params: {
                user,
              },
            },
          ],
        }),
      );
    }
  }

  render() {
    return <FullScreenLoader />;
  }
}
