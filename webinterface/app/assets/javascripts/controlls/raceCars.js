var raceCars = (function (totalLaps) {

    var _speedToPercentage = (100 / 255),
        _currentSpeed = [0, 0],
        _currentSteer = [1, 1],
        _totalLaps = totalLaps,
        _driverLapTimes = [new Array(totalLaps), new Array(totalLaps)],
        _driverLaps = [0, 0],
        _driverFastestLap = [null, null],
        _currentLap = 0;


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
        _updateLaptime(carNr, lapData);
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
        if (_currentSpeed[carNr - 1] != speed) {
            _currentSpeed[carNr - 1] = speed;

            $('#car_speedLbl_' + carNr).text(speed);

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

    /**
     * Updates the lapTime
     * @param carNr
     * @param lapData
     * @private
     */
    var _updateLaptime = function (carNr, lapData) {

        // race didnt start and the car did not started its first lap
        if (_driverLaps[carNr - 1] == 0 && lapData[1] != 1) {
            return;
        }

        // the current readed lap time
        var lapTime = Number(lapData[0]);
        var formatedTime = lapTime / 1000;


        if (_driverLaps[carNr] != 0) {
            $('#driverLapTime_' + carNr).text(formatedTime);
        }


        // car hit the laptimer
        if (lapData[1] == 1) {
            $('#driverLap_' + carNr).text(_driverLaps[carNr - 1]+1);

            // car finished round
            if (_driverLaps[carNr - 1] >= 1) {

                // fastest lap ?
                if(_driverFastestLap[carNr -1] == null || _driverFastestLap[carNr -1] > lapTime) {
                    _driverFastestLap[carNr -1] = lapTime;
                    $('#driverLapFastest_'+carNr).text(formatedTime)
                }

                _driverLapTimes[carNr-1][_driverLaps[carNr - 1] -1] = lapTime;

                $('#driverLap_' + carNr + '' + _driverLaps[carNr - 1]).text(formatedTime);

                // check if we have to update the current lap
                if (_currentLap < _driverLaps[carNr - 1]) {
                    _currentLap = _driverLaps[carNr - 1];
                    $('#raceCurrentLap').text(_currentLap);

                    if(_currentLap == totalLaps) {
                        alert('Driver: '+carNr+" finished Race");
                        console.error(_driverLapTimes)
                    }
                }
            }
            _driverLaps[carNr - 1]++;
        }
    }

    return {
        onData: _onData
    }
});