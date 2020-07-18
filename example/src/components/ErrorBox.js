import React from 'react';
import styled from 'styled-components';

export default function ErrorBox({errors = [], style = {}} = {}) {
  const texts = errors.map((error, index) => {
    return <ErrorText key={String(index)}>○ {error}</ErrorText>;
  });
  return <Container style={style}>{texts}</Container>;
}

const Container = styled.View`
  margin: 0 15px;
  padding: 15px 10px;
  background-color: rgba(183, 28, 28, 0.2);
  border-radius: 8px;
`;

const ErrorText = styled.Text`
  color: #000;
  line-height: 24px;
  color: #b71c1c;
`;
