import { NativeModules, Platform } from 'react-native';

function _extends() {
  _extends = Object.assign || function (target) {
    for (var i = 1; i < arguments.length; i++) {
      var source = arguments[i];

      for (var key in source) {
        if (Object.prototype.hasOwnProperty.call(source, key)) {
          target[key] = source[key];
        }
      }
    }

    return target;
  };

  return _extends.apply(this, arguments);
}

function _inheritsLoose(subClass, superClass) {
  subClass.prototype = Object.create(superClass.prototype);
  subClass.prototype.constructor = subClass;
  subClass.__proto__ = superClass;
}

function _getPrototypeOf(o) {
  _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) {
    return o.__proto__ || Object.getPrototypeOf(o);
  };
  return _getPrototypeOf(o);
}

function _setPrototypeOf(o, p) {
  _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) {
    o.__proto__ = p;
    return o;
  };

  return _setPrototypeOf(o, p);
}

function _isNativeReflectConstruct() {
  if (typeof Reflect === "undefined" || !Reflect.construct) return false;
  if (Reflect.construct.sham) return false;
  if (typeof Proxy === "function") return true;

  try {
    Date.prototype.toString.call(Reflect.construct(Date, [], function () {}));
    return true;
  } catch (e) {
    return false;
  }
}

function _construct(Parent, args, Class) {
  if (_isNativeReflectConstruct()) {
    _construct = Reflect.construct;
  } else {
    _construct = function _construct(Parent, args, Class) {
      var a = [null];
      a.push.apply(a, args);
      var Constructor = Function.bind.apply(Parent, a);
      var instance = new Constructor();
      if (Class) _setPrototypeOf(instance, Class.prototype);
      return instance;
    };
  }

  return _construct.apply(null, arguments);
}

function _isNativeFunction(fn) {
  return Function.toString.call(fn).indexOf("[native code]") !== -1;
}

function _wrapNativeSuper(Class) {
  var _cache = typeof Map === "function" ? new Map() : undefined;

  _wrapNativeSuper = function _wrapNativeSuper(Class) {
    if (Class === null || !_isNativeFunction(Class)) return Class;

    if (typeof Class !== "function") {
      throw new TypeError("Super expression must either be null or a function");
    }

    if (typeof _cache !== "undefined") {
      if (_cache.has(Class)) return _cache.get(Class);

      _cache.set(Class, Wrapper);
    }

    function Wrapper() {
      return _construct(Class, arguments, _getPrototypeOf(this).constructor);
    }

    Wrapper.prototype = Object.create(Class.prototype, {
      constructor: {
        value: Wrapper,
        enumerable: false,
        writable: true,
        configurable: true
      }
    });
    return _setPrototypeOf(Wrapper, Class);
  };

  return _wrapNativeSuper(Class);
}

var RNBackgroundGeofencingException = /*#__PURE__*/function (_Error) {
  _inheritsLoose(RNBackgroundGeofencingException, _Error);

  function RNBackgroundGeofencingException(error) {
    var _this;

    _this = _Error.call(this, error.message) || this;
    _this.name = 'RNBackgroundGeofencingException';
    _this.message = error.message;
    _this.code = error.code;
    return _this;
  }

  return RNBackgroundGeofencingException;
}( /*#__PURE__*/_wrapNativeSuper(Error));

/**
 * Configures a webhook that the device will use to send Geofence events via POST request
 * @param {RNBackgroundGeofencingWebhook}
 */
var configureWebhook = function configureWebhook(webhook) {
  try {
    return Promise.resolve(withPlatformSupport(function () {
      if (typeof (webhook === null || webhook === void 0 ? void 0 : webhook.url) !== 'string') {
        throw new RNBackgroundGeofencingException({
          code: 'geofence_exception',
          message: 'A valid url is required to configure a webhook'
        });
      }

      return RNBackgroundGeofencing.configureWebhook(_extends({}, DEFAULT_WEBHOOK_CONFIGURATION, webhook));
    }, false));
  } catch (e) {
    return Promise.reject(e);
  }
};
var BackgroundGeofencing = NativeModules.BackgroundGeofencing;
var SUPPORTED_PLATFORMS = ['android'];
var TAG = '[RNBackgroundGeofencing]: ';

var RNBackgroundGeofencing = /*#__PURE__*/_extends({}, BackgroundGeofencing);

var DEFAULT_WEBHOOK_CONFIGURATION = {
  url: null,
  headers: {},
  timeout: 15000
};

var withPlatformSupport = function withPlatformSupport(wrappedFunction, errorValue) {
  var currentPlatform = Platform.OS;

  if (SUPPORTED_PLATFORMS.includes(currentPlatform)) {
    return wrappedFunction();
  } else {
    console.warn(TAG + ("This library doesn't support " + currentPlatform + " devices"));
    return errorValue;
  }
};

export { configureWebhook };
//# sourceMappingURL=react-native-background-geofencing.esm.js.map
