/**
 * Simple view cars control with gauges yeah
 * @type {Function}
 */
var simpleCars = (function () {

    /**
     * The car settings obj
     * @type {null}
     * @private
     */
    var _carSettings = null;


    /**
     * Colors for the gradients
     * @type {*[]}
     * @private
     */
    var _gradColors = [ new steelseries.rgbaColor(0, 0, 200, 1),
        new steelseries.rgbaColor(0, 200, 0, 1),
        new steelseries.rgbaColor(200, 200, 0, 1),
        new steelseries.rgbaColor(200, 0, 0, 1),
        new steelseries.rgbaColor(200, 0, 0, 1) ];

    /**
     * Gradient sections
     * @type {number[]}
     * @private
     */
    var _gradSections = [ 0, 0.33, 0.66, 0.85, 1];

    /**
     * Gradient for the power gui element
     * @type {steelseries.gradientWrapper}
     * @private
     */
    var _powerGrad = new steelseries.gradientWrapper(0, 250, _gradSections, _gradColors);

    var fuelGrad = new steelseries.gradientWrapper(0, 10000, _gradSections, _gradColors);

    /**
     * Holds the gui controlls for a car
     * @type {{car1: {}, car2: {}}}
     * @private
     */
    var _carControls = {
        car1: {
            lowOnFuel: false
        },
        car2: {
            lowOnFuel: false
        }
    }

    var _lowFuelSound = new Audio('/assets/sounds/low_fuel.mp3');

    /**
     * Setups the gui by the carsettings
     * @param carSettings
     * @private
     */
    var _setup = function (carSettings) {
        _carSettings = carSettings;
        _setupCarControlls(1);
        _setupCarControlls(2);
    }

    /**
     * Setups the gui controlls for a car
     * @param carNr
     */
    var _setupCarControlls = function (carNr) {

        var carName = 'car' + carNr;
        var carHeadline = 'Car ' + carNr;
        carHeadline += (_carSettings[carName].ghostCar) ? '(Ghost)' : '';

        $('#carHeadline_' + carNr).html(carHeadline);

        _carControls[carName].speedGauge = new steelseries.RadialBargraph('speedCanvas_' + carNr, {
            gaugeType: steelseries.GaugeType.TYPE4,
            size: 201,
            titleString: 'Car ' + carNr,
            unitString: 'Power',
            valueGradient: _powerGrad,
            useValueGradient: true,
            threshold: 50,
            lcdVisible: true,
            minValue: 0,
            maxValue: 250
        });

        _carControls[carName].steerLeft = new steelseries.Led('ledSteerLeft_' + carNr);
        _carControls[carName].steerRight = new steelseries.Led('ledSteerRight_' + carNr, {
            ledColor: steelseries.LedColor.GREEN_LED
        });

        _carControls[carName].fuelGauge = new steelseries.LinearBargraph('fuelCanvas_' + carNr, {
            width: 320,
            height: 140,
            valueGradient: fuelGrad,
            useValueGradient: true,
            titleString: 'Car ' + carName,
            unitString: 'Fuel',
            threshold: _carSettings[carName].fuelReserve,
            lcdVisible: true,
            minValue: 0,
            maxValue: _carSettings[carName].fuelFull
        });

        _carControls[carName].laptimer = new steelseries.DisplaySingle('laptimerCanvas_' + carNr, {
            width: 120,
            height: 50
        });

        _carControls[carName].lastLaptimer = new steelseries.DisplaySingle('lastLaptimerCanvas_' + carNr, {
            width: 120,
            height: 50,
            lcdColor: steelseries.LcdColor.YELLOW

        });

        _carControls[carName].fastesLaptimer = new steelseries.DisplaySingle('fastesLaptimerCanvas_' + carNr, {
            width: 120,
            height: 50,
            lcdColor: steelseries.LcdColor.RED
        });
    }

    /**
     * Updates the data for the car
     * @param carNr
     * @param data
     * @param lapData
     */
    var _onData = function (carNr, data, lapData) {
        var carName = 'car' + carNr;
        _carControls[carName].speedGauge.setValue(data[0]);

        if (data[1] == 1) {
            _carControls[carName].steerLeft.setLedOnOff(false);
            _carControls[carName].steerRight.setLedOnOff(true);
        } else {
            _carControls[carName].steerLeft.setLedOnOff(true);
            _carControls[carName].steerRight.setLedOnOff(false);
        }

        if (_carSettings[carName].careForFuel == 1) {
            if (data[3] > 0) {
                // how many time is left
                var percentage = data[3] / (_carSettings[carName].refillTime / 100);
                // how much is fueld up
                var value = _carSettings[carName].fuelFull - ((_carSettings[carName].fuelFull / 100) * percentage)
                _carControls[carName].fuelGauge.setValue(value);
            } else {
                _carControls[carName].fuelGauge.setValue(data[2]);

                // play the lowfuel sound ?
                if (_carSettings[carName].fuelReserve > Number(data[2]) && _carControls[carName].lowOnFuel == false) {
                    _carControls[carName].lowOnFuel = true;
                    _lowFuelSound.play();
                }

                // reset flag for lowFuel
                if (_carSettings[carName].fuelReserve < Number(data[2]) && _carControls[carName].lowOnFuel == true) {
                    _carControls[carName].lowOnFuel = false;
                }
            }
        }

        var lapTime = Number(lapData[0]);
        _carControls[carName].laptimer.setValue(lapTime / 1000);

        // car ended a lap
        if (lapData[1] == 1) {
            if (_carControls[carName].fastesLap == null || _carControls[carName].fastesLap > lapTime) {
                _carControls[carName].fastesLap = lapTime;
                _carControls[carName].fastesLaptimer.setValue(_carControls[carName].fastesLap / 1000);
            }

            _carControls[carName].lastLaptimer.setValue(lapTime / 1000);
        }
    }

    return {
        setup: _setup,
        onData: _onData
    }
});