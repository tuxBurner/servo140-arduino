/**
 * Handles all the websocket comunication
 * @type {Function}
 */
var communications = (function () {
    /**
     * Settings from the backend for the cars
     * @type {null}
     * @private
     */
    var _carSettings = null;

    var _wsUri = jsRoutes.controllers.ApplicationController.joinRoomWs().webSocketURL();
    var _websocket = null;
    var _controllReady = false;
    /**
     * Is called after the settings are parsed
     * @private
     */
    var _setupGui = function () {
        alert("override me with com.setupGui = function() {};")
    };

    /**
     * callback for the startlight display
     * @param lightNr the number of the light between 1 and 4 or -1 when none is displayed
     * @private
     */
    var _lightControll = function (lightNr) {
        console.error(lightNr);
    };

    /**
     * callback for the power controll
     * @param powerState when 1 thenn power is on else it is turned off
     * @private
     */
    var _powerControll = function (powerState) {
    };


    /**
     * Updates the display data off the car
     * @param carNr the number off the car 1 or 2
     * @param data  data[0] speed / data[1] == 1 steer right / data[2] fuel amount / data[3] > 0 then refill is in process     *
     * @param lapData lapData[0] the current laptime / lapData[1] == 1 then car ended a lap
     * @private
     */
    var _updateCarControlls = function (carNr, data, lapData) {
    };

    /**
     * Starts the communication
     * @private
     */
    var _start = function () {

        _websocket = new WebSocket(_wsUri);

        _websocket.onopen = function (evt) {
            // first we want the settings here
            _callBackendForSettings();
        };

        _websocket.onclose = function (evt) {
        };

        _websocket.onmessage = function (evt) {
            // we received settings
            if (evt.data.indexOf("S") == 0) {
                _parseSettingsToSettings(evt.data);
            } else {
                //wait until controlls are ready
                if (_controllReady == true) {
                    _onMessage(evt.data)
                }
            }
        };

        _websocket.onerror = function (evt) {
            websocket.close();
            alert("An error happened at the websocket.");
        };
    }


    /**
     * Tells the backend to get settings from the arduino
     */
    var _callBackendForSettings = function () {
        pAjax(jsRoutes.controllers.ApplicationController.getSerialSettings(), null, function (data) {
        }, function (data) {
        });

    }

    /**
     * Calls the backend to powerOff the system
     */
    var _powerOff = function () {
        pAjax(jsRoutes.controllers.ApplicationController.powerOff(), null, function (data) {
        }, function (data) {
        });
    }

    /**
     * Calls the backend to start the light sequence
     */
    var _startLightSeq = function () {
        pAjax(jsRoutes.controllers.ApplicationController.startLightSeq(), null, function (data) {
        }, function (data) {
        });
    }

    /**
     * Parses the settings of the arduino to js settings
     */
    function _parseSettingsToSettings(backendSettings) {
        var split = backendSettings.split(',');
        _carSettings = {
            car1: {
                ghostCar: split[1] == 1,
                thrust: split[2],
                steerRight: split[3] == 1,
                careForFuel: split[4] == 1,
                fuelFull: split[5],
                fuelReserve: split[6],
                refillTime: split[7]
            },
            car2: {
                ghostCar: split[8] == 1,
                thrust: split[9],
                steerRight: split[10] == 1,
                careForFuel: split[11] == 1,
                fuelFull: split[12],
                fuelReserve: split[13],
                refillTime: split[14]
            }
        }

        // setup the gui
        _setupGui();
    }

    /**
     * Is called when data arrived over the websocket
     * @param data
     */
    var _onMessage = function (data) {
        var data = data.split(',');
        if (data.length == 14) {
            _lightControll(data[0]);
            _powerControll(data[1]);
            _updateCarControlls(1, data.slice(2, 6), data.slice(10, 12));
            _updateCarControlls(2, data.slice(6, 10), data.slice(12, 14));
        }
    }

    return {
        start: _start,
        powerOff: _powerOff,
        startLightSeq: _startLightSeq,
        setupGui: function (func) {
            _setupGui = function () {
                func();
                _controllReady = true;
            }
        },
        setLightControll: function (func) {
            _lightControll = func;
        },
        setPowerControll: function (func) {
            _powerControll = func;
        },
        setUpdateCarControlls: function (func) {
            _updateCarControlls = func;
        }
    }


});