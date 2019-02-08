/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 * @lint-ignore-every XPLATJSCOPYRIGHT1
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View} from 'react-native';
import {NativeModules} from 'react-native';

export default class App extends Component {
  state = {
    message: ''
  }

  componentDidMount() {
    NativeModules.IoT.pingIoT(10.0058822, 76.3066627, (err, name) => {
      this.setState({message: err});
    });
    
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.instructions}>{this.state.message}</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});
