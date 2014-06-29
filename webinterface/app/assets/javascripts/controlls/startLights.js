/**
 * Default handler for startlights
 * @type {Function}
 */
var startLights = (function () {
    var _startLight1,
        _startLight2,
        _startLight3,
        _startLight4,
        _currentLight = null,
        _beepSound = null,
        _goSound = null;

    /**
     * Setups the startlights
     * @private
     */
    var _setup = function () {

        _beepSound = new Audio('/assets/sounds/beep.wav');
        _goSound = new Audio('/assets/sounds/go.wav');

        // startlight
        _startLight1 = new steelseries.Led('startLight1Canvas');
        _startLight2 = new steelseries.Led('startLight2Canvas', {
            ledColor: steelseries.LedColor.ORANGE_LED
        });
        _startLight3 = new steelseries.Led('startLight3Canvas', {
            ledColor: steelseries.LedColor.YELLOW_LED
        });
        _startLight4 = new steelseries.Led('startLight4Canvas', {
            ledColor: steelseries.LedColor.GREEN_LED
        });
    }

    /**
     * Call this when there is some data for the light
     * @param lightNr
     * @private
     */
    var _onData = function (lightNr) {
        if (_currentLight != lightNr) {
            _currentLight = lightNr;
            if (lightNr == -1) {
                _startLight1.setLedOnOff(false);
                _startLight2.setLedOnOff(false);
                _startLight3.setLedOnOff(false);
                _startLight4.setLedOnOff(false);
                _goSound.play();
            }

            if (lightNr == 1) {
                _startLight1.setLedOnOff(true);
                _beepSound.play();
            }
            if (lightNr == 2) {
                _startLight2.setLedOnOff(true);
                _beepSound.play();
            }
            if (lightNr == 3) {
                _startLight3.setLedOnOff(true);
                _beepSound.play();
            }
            if (lightNr == 4) {
                _startLight4.setLedOnOff(true);
                _beepSound.play();
            }
        }
    }

    return {
        setup: _setup,
        onData: _onData
    }
});