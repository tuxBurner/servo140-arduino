var websocket = null;
var settings = null;

$(function () {
    /**
     * When the settings button is clicked we
     */
    $('#settingsBtn').click(function () {

    });

    var wsUri = jsRoutes.controllers.ApplicationController.joinRoomWs().webSocketURL();
    websocket = new WebSocket(wsUri);

    websocket.onopen = function (evt) {
        // first we want the settings here
        callBackendForSettings();
    };

    websocket.onclose = function (evt) {
    };

    websocket.onmessage = function (evt) {
        // we received settings
        if (evt.data.indexOf("S") == 0) {
            parseSettingsToSettings(evt.data);
        } else {
            onMessage(evt.data)
        }
    };

    websocket.onerror = function (evt) {
        onError(evt)
    };

    function onError(evt) {
        websocket.close();
    }
});

/**
 * Tells the backend to get settings from the arduino
 */
function callBackendForSettings() {
    pAjax(jsRoutes.controllers.ApplicationController.getSerialSettings(), null, function (data) {
    }, function (data) {
    });
}

/**
 * Parses the settings of the arduino to js settings
 */
function parseSettingsToSettings(backendSettings) {
    var split = backendSettings.split(',');
    settings = {
        car1: {
            ghostCar: split[1] == 1,
            thrust: split[2],
            steerRight: split[3],
            fuelFull: split[4],
            fuelReserve: split[5],
            refillTime: split[6]
        },
        car2: {
            ghostCar: split[7] == 1,
            thrust: split[8],
            steerRight: split[9],
            fuelFull: split[10],
            fuelReserve: split[11],
            refillTime: split[12]
        }
    }
}

/**
 * This function handles an ajax call to an javascript route which is configured
 */
function pAjax(controller, fnData, sucessFn, errorFn) {
    controller.ajax({
        data: fnData,
        success: function (data) {
            sucessFn(data);
        },
        error: function (data) {
            // TODO: on error and no errorFn alert here something
            errorFn(data)
        }
    });
}