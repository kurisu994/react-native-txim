import { NativeModules, Platform } from 'react-native';

const { IMInitializeModule: module } = NativeModules;

const eventName = (name) => {
  if (Platform.OS === 'ios') {
    // noinspection JSUnresolvedVariable
    return module.EventName[name];
  } else {
    return module[name];
  }
};

export default {
  loginStatus: eventName('loginStatus'),
  initializeStatus: eventName('initializeStatus'),
  userStatus: eventName('userStatus'),
  sendStatus: eventName('sendStatus'),
  onNewMessage: eventName('onNewMessage'),
  conversationStatus: eventName('conversationStatus'),
  conversationListStatus: eventName('conversationListStatus'),
  onConversationRefresh: eventName('onConversationRefresh'),
  onMessageQuery: eventName('onMessageQuery'),
};
