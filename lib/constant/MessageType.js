import { NativeModules, Platform } from 'react-native';

const { IMInitializeModule: module } = NativeModules;

const msgType = (name) => {
  if (Platform.OS === 'ios') {
    // noinspection JSUnresolvedVariable
    return module.MessageType[name];
  } else {
    return module[name];
  }
};

export default {
  Text: msgType('Text'),
  Image: msgType('Image'),
  Sound: msgType('Sound'),
  Video: msgType('Video'),
  File: msgType('File'),
  Location: msgType('Location'),
  Face: msgType('Face'),
  Custom: msgType('Custom'),
};
