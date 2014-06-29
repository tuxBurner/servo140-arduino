var raceCars = (function () {

    var _speedToPercentage = (100 / 255),
        _currentSpeed = new Array(2),
        _currentSteer = new Array(2);

    /**
     * Is called when data for a car is avaible
     * @param carNr
     * @param data
     * @param lapData
     * @private
     */
    var _onData = function (carNr, data, lapData) {
        _updateSpeed(carNr, data[0]);
        _updateSteering(carNr, data[1]);
    }

    /**
     * Updates the steering visuals
     * @param carNr
     * @param steer
     * @private
     */
    var _updateSteering = function (carNr, steer) {
        if (_currentSteer[carNr] != steer) {
            _currentSteer[carNr] = steer;
            if (steer == 1) {
                $('#car_steerLeft_' + carNr).addClass('disabled')
                $('#car_steerRight_' + carNr).removeClass('disabled')
            } else {
                $('#car_steerLeft_' + carNr).removeClass('disabled')
                $('#car_steerRight_' + carNr).addClass('disabled')
            }
        }
    }

    /**
     * Updates the speed visual
     * @param carNr
     * @param speed
     * @private
     */
    var _updateSpeed = function (carNr, speed) {

        if (_currentSpeed[carNr] != speed) {
            _currentSpeed[carNr] = speed;

            $('#car_speedLbl_' + carNr).html(speed);

            if (speed == 0) {
                $('#car_speedLow_' + carNr).css('width', '0%');
                $('#car_speedMid_' + carNr).css('width', '0%');
                $('#car_speedHigh_' + carNr).css('width', '0%');
                return;
            }

            if (speed > 170) {
                var percentage = _speedToPercentage * (speed - 170);
                $('#car_speedLow_' + carNr).css('width', '33%');
                $('#car_speedMid_' + carNr).css('width', '33%');
                $('#car_speedHigh_' + carNr).css('width', percentage + '%');
                return;
            }

            if (speed > 85) {
                var percentage = _speedToPercentage * (speed - 85);
                $('#car_speedLow_' + carNr).css('width', '33%');
                $('#car_speedMid_' + carNr).css('width', percentage + '%');
                $('#car_speedHigh_' + carNr).css('width', '0%');
                return;
            }

            if (speed <= 85) {
                var percentage = _speedToPercentage * speed;
                $('#car_speedLow_' + carNr).css('width', percentage + '%');
                $('#car_speedMid_' + carNr).css('width', '0%');
                $('#car_speedHigh_' + carNr).css('width', '0%');
                return;
            }
        }


    }

    return {
        onData: _onData
    }
});