var websocket = null;
var settings = null;
var controllReady = false;

$(function () {
    /**
     * When the settings button is clicked we display the settings modal
     */
    $('#settingsBtn').click(function () {
        setCarSettingsToModal('1');
        setCarSettingsToModal('2');
        $('#settingsModal').modal('show');
    });


    /**
     * Update the value when changing the slider
     */
    $('[type="range"]').on("change", function () {
        var thisId = $(this).attr('id');
        $("#val_" + thisId).html($(this).val());
    });

    /**
     * Take care of saving the settings to the arduino
     */
    $('#saveSettingsBtn').click(function() {
      var settingsStr =  readSettingsForCar(1) + ',' +readSettingsForCar(2);;
        pAjax(jsRoutes.controllers.ApplicationController.setSerialSettings(settingsStr), null, function (data) {
            $('#settingsModal').modal('hide');
        }, function (data) {
        });

    });

    websocketMagic();
});

/**
 * Reads the settings for the given car
 * @param carNr
 * @returns {string}
 */
var readSettingsForCar = function(carNr) {
    var carSttingsStr = "";
    carSttingsStr+=($('#ghostCar_'+carNr).prop('checked')) ? '1' : '0';
    carSttingsStr+=',';
    carSttingsStr+=$('#thrust_'+carNr).val();
    carSttingsStr+=',';
    carSttingsStr+=($('#steerRight_'+carNr).prop('checked')) ? '1' : '0';
    carSttingsStr+=',';
    carSttingsStr+=($('#carForFuel_'+carNr).prop('checked')) ? '1' : '0';
    carSttingsStr+=',';
    carSttingsStr+=$('#fuelFull_'+carNr).val();
    carSttingsStr+=',';
    carSttingsStr+=$('#fuelReserve_'+carNr).val();
    carSttingsStr+=',';
    carSttingsStr+=$('#refillTime_'+carNr).val() * 1000;

    return carSttingsStr;
}

/**
 * This nurse takes care of the websocket :)
 */
var websocketMagic = function () {
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

            // start the controll
            controllStart();
        } else {
            if (controllReady == true) {
                onMessage(evt.data)
            }
        }
    };

    websocket.onerror = function (evt) {
        websocket.close();
    };
}

/**
 * Tells the backend to get settings from the arduino
 */
function callBackendForSettings() {
    pAjax(jsRoutes.controllers.ApplicationController.getSerialSettings(), null, function (data) {
    }, function (data) {
    });
}

/**
 * Sets the settings of the given car to the modal settings window
 * @param carNr
 */
var setCarSettingsToModal = function (carNr) {
    var carSettings = settings["car" + carNr];

    $('#steerRight_' + carNr).prop("checked", carSettings.steerRight);
    $('#ghostCar_' + carNr).prop("checked", carSettings.ghostCar);

    setRangeVal('thrust_' + carNr, carSettings.thrust);

    $('#carForFuel_' + carNr).prop("checked", carSettings.careForFuel);

    setRangeVal('fuelFull_' + carNr, carSettings.fuelFull);
    setRangeVal('fuelReserve_' + carNr, carSettings.fuelReserve);
    setRangeVal('refillTime_' + carNr, carSettings.refillTime / 1000);
}

/**
 * Sets the value for a range input
 * @param id
 * @param value
 */
var setRangeVal = function (id, value) {
    $('#' + id).val(value);
    $('#val_' + id).html(value);
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