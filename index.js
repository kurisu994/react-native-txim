const Message = require('./im/Messge');

const Init = require('./im/ImInit');



module.exports = {

  ...Message.default,

  ...Init.default

}