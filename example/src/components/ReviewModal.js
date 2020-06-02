import React from 'react';
import styled from 'styled-components';
import {TouchableOpacity} from 'react-native';
import {Picker} from '@react-native-community/picker';

export default class ReviewModal extends React.Component {
  state = {
    quality: 1,
    description: '',
    error: false,
  };

  handleOnCancelPress = () => {
    const {onCloseRequest} = this.props;
    if (onCloseRequest) {
      onCloseRequest();
    }
  };

  handleOnSubmitPress = () => {
    const {onSubmit} = this.props;
    const {description, quality} = this.state;
    if (!description) {
      this.setState({error: true});
    } else if (onSubmit) {
      onSubmit({description, quality});
    }
  };

  render() {
    const formStyles = {elevation: 5};
    const {quality, description, error} = this.state;
    return (
      <Background>
        <Form style={formStyles}>
          <InputGroup>
            <Title>REVIEW</Title>
          </InputGroup>
          <InputGroup>
            <Label>What is the quality of this event?</Label>
            <Picker
              mode="dropdown"
              selectedValue={quality}
              onValueChange={value => this.setState({quality: value})}>
              <Picker.Item label="👍 (Good)" value={1} />
              <Picker.Item label="😐 (Meh)" value={0.5} />
              <Picker.Item label="👎 (Bad)" value={0} />
            </Picker>
          </InputGroup>
          <InputGroup>
            <Label>Any other information you can provide?</Label>
          </InputGroup>
          <InputGroup>
            <TextArea
              error={error}
              multiline
              placeholder="This event is spot on! I had my data and location turned on"
              maxHeight={60}
              value={description}
              onChangeText={text => this.setState({description: text})}
            />
          </InputGroup>
          <ButtonBarContainer>
            <TouchableOpacity onPress={this.handleOnCancelPress}>
              <ButtonText>CANCEL</ButtonText>
            </TouchableOpacity>
            <TouchableOpacity onPress={this.handleOnSubmitPress}>
              <ButtonText>SUBMIT</ButtonText>
            </TouchableOpacity>
          </ButtonBarContainer>
        </Form>
      </Background>
    );
  }
}

const TextArea = styled.TextInput`
  border: 1px solid ${props => (props.error ? '#ef9a9a' : '#e0e0e0')};
  margin-top: 15px;
  border-radius: 4px;
  padding-left: 15px;
  padding-right: 15px;
  font-size: 16px;
`;

const Background = styled.View`
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
`;

const Form = styled.View`
  background-color: white;
  border-radius: 4px;
  padding: 25px;
  width: 330px;
`;

const Title = styled.Text`
  font-size: 18px;
  font-weight: bold;
  opacity: 0.9;
  margin-bottom: 10px;
`;

const Label = styled.Text`
  font-size: 16px;
`;

const ButtonBarContainer = styled.View`
  margin-top: 30px;
  flex-direction: row;
  justify-content: flex-end;
`;

const ButtonText = styled.Text`
  padding: 0 25px;
  padding-right: 0;
  color: #0d47a1;
  font-weight: bold;
  opacity: 0.8;
`;

const InputGroup = styled.View``;
