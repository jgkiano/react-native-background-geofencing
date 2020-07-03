
'use strict'

if (process.env.NODE_ENV === 'production') {
  module.exports = require('./react-native-background-geofencing.cjs.production.min.js')
} else {
  module.exports = require('./react-native-background-geofencing.cjs.development.js')
}
