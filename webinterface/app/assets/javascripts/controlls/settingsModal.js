/**
 * Handles the settings modal
 * @type {Function}
 */
var settingsModal = (function () {


    /**
     * Holds the settings for a car
     * @type {null}
     * @private
     */
    var _carSettings = null;

    /**
     * Setups this component
     * @private
     */
    var _setup = function() {
        /**
         * When the settings button is clicked we display the settings modal
         */
        $('#settingsBtn').click(function () {
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
        $('#saveSettingsBtn').click(function () {
            var settingsStr = _readSettingsForCar(1) + ',' + _readSettingsForCar(2);
            pAjax(jsRoutes.controllers.ApplicationController.setSerialSettings(settingsStr), null, function (data) {
                window.location.reload();
            }, function (data) {
            });

        });
    }


    /**
     * Reads the settings for the given car from the dom tree
     * @param carNr
     * @returns {string}
     */
    var _readSettingsForCar = function (carNr) {
        var carSttingsStr = "";
        carSttingsStr += ($('#ghostCar_' + carNr).prop('checked')) ? '1' : '0';
        carSttingsStr += ',';
        carSttingsStr += $('#thrust_' + carNr).val();
        carSttingsStr += ',';
        carSttingsStr += ($('#steerRight_' + carNr).prop('checked')) ? '1' : '0';
        carSttingsStr += ',';
        carSttingsStr += ($('#carForFuel_' + carNr).prop('checked')) ? '1' : '0';
        carSttingsStr += ',';
        carSttingsStr += $('#fuelFull_' + carNr).val();
        carSttingsStr += ',';
        carSttingsStr += $('#fuelReserve_' + carNr).val();
        carSttingsStr += ',';
        carSttingsStr += $('#refillTime_' + carNr).val() * 1000;

        return carSttingsStr;
    }

    /**
     * Call this when the settings from the backend changed
     * @param carSettings
     * @private
     */
    var _setSettings = function(carSettings) {
        _carSettings = carSettings;
        _setCarSettingsToModal('1');
        _setCarSettingsToModal('2');
    }

    /**
     * Sets the settings of the given car to the modal settings window
     * @param carNr
     */
    var _setCarSettingsToModal = function (carNr) {
        var carSettings = _carSettings["car" + carNr];

        $('#steerRight_' + carNr).prop("checked", carSettings.steerRight);
        $('#ghostCar_' + carNr).prop("checked", carSettings.ghostCar);

        _setRangeVal('thrust_' + carNr, carSettings.thrust);

        $('#carForFuel_' + carNr).prop("checked", carSettings.careForFuel);

        _setRangeVal('fuelFull_' + carNr, carSettings.fuelFull);
        _setRangeVal('fuelReserve_' + carNr, carSettings.fuelReserve);
        _setRangeVal('refillTime_' + carNr, carSettings.refillTime / 1000);
    }

    /**
     * Sets the value for a range input
     * @param id
     * @param value
     */
    var _setRangeVal = function (id, value) {
        $('#' + id).val(value);
        $('#val_' + id).html(value);
    }


    _setup();

    return {
        setSettings: _setSettings
    }


});