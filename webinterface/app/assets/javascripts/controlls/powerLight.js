/**
 * Default handler for startlights
 * @type {Function}
 */
var powerLight = (function () {
    var _powerLight,
        _currentPowerState,
        _powerOffSound;

    /**
     * Setups the startlights
     * @private
     */
    var _setup = function () {
        _powerOffSound = new Audio('assets/sounds/powerOff.wav');
        _powerLight = new steelseries.Led('powerLightCanvas', {
            width: 200,
            height: 200
        });
    }

    /**
     * Call this when there is some data for the powerState
     * @param state
     * @private
     */
    var _onData = function (state) {
        if (_currentPowerState != state) {
            _currentPowerState = state;
            if (_currentPowerState == 1) {
                _powerLight.setLedColor(steelseries.LedColor.GREEN_LED);
                _powerLight.blink(false);
                _powerLight.setLedOnOff(true);
            } else {
                _powerOffSound.play();
                _powerLight.setLedColor(steelseries.LedColor.RED_LED);
                _powerLight.blink(true);
            }
        }
    }

    return {
        setup: _setup,
        onData: _onData,
        currentPowerState: _currentPowerState
    }
});