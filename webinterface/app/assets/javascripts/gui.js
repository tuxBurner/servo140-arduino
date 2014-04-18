var startLight1,
    startLight2,
    startLight3,
    startLight4,
    powerLight;

var carControls = {
    car1: {
    },
    car2: {
    }
}

var gradColors = [ new steelseries.rgbaColor(0, 0, 200, 1),
    new steelseries.rgbaColor(0, 200, 0, 1),
    new steelseries.rgbaColor(200, 200, 0, 1),
    new steelseries.rgbaColor(200, 0, 0, 1),
    new steelseries.rgbaColor(200, 0, 0, 1) ];

var gradSections = [ 0, 0.33, 0.66, 0.85, 1];

var powerGrad = new steelseries.gradientWrapper(0, 250, gradSections, gradColors);
var fuelGrad = new steelseries.gradientWrapper(0, 10000, gradSections, gradColors);


/**
 * Is called when new settings are coming from the backend
 */
var controllStart = function () {

    // startlight
    startLight1 = new steelseries.Led('startLight1Canvas');
    startLight2 = new steelseries.Led('startLight2Canvas', {
        ledColor: steelseries.LedColor.ORANGE_LED
    });
    startLight3 = new steelseries.Led('startLight3Canvas', {
        ledColor: steelseries.LedColor.YELLOW_LED
    });
    startLight4 = new steelseries.Led('startLight4Canvas', {
        ledColor: steelseries.LedColor.GREEN_LED
    });
    // power bulb
    powerLight = new steelseries.Led('powerCanvas', {
        width: 200,
        height: 200
    });

    setupCarControlls(1);
    setupCarControlls(2);

    controllReady = true;
}

/**
 * Setups the car controll
 * @param carNr
 */
var setupCarControlls = function (carNr) {

    var carName = 'car' + carNr;
    var carSettings = settings[carName];

    carControls[carName].speedGauge = new steelseries.RadialBargraph('speedCanvas_' + carNr, {
        gaugeType: steelseries.GaugeType.TYPE4,
        size: 201,
        titleString: 'Car ' + carNr,
        unitString: 'Power',
        valueGradient: powerGrad,
        useValueGradient: true,
        threshold: 50,
        lcdVisible: true,
        minValue: 0,
        maxValue: 250
    });

    carControls[carName].fuelGauge = new steelseries.LinearBargraph('fuelCanvas_' + carNr, {
        width: 320,
        height: 140,
        valueGradient: fuelGrad,
        useValueGradient: true,
        titleString: 'Car ' + carName,
        unitString: 'Fuel',
        threshold: carSettings.fuelReserve,
        lcdVisible: true,
        minValue: 0,
        maxValue: carSettings.fuelFull
    });

    carControls[carName].laptimer = new steelseries.DisplaySingle('laptimerCanvas_' + carNr, {
        width: 120,
        height: 50
    });

    carControls[carName].lastLaptimer = new steelseries.DisplaySingle('lastLaptimerCanvas_' + carNr, {
        width: 120,
        height: 50
    });

    carControls[carName].fastesLaptimer = new steelseries.DisplaySingle('fastesLaptimerCanvas_' + carNr, {
        width: 120,
        height: 50
    });

}

/**
 * Updates the data for the car
 * @param carNr
 * @param data
 * @param lapData
 */
var updateCarControlls = function (carNr, data, lapData) {
    var carName = 'car' + carNr;
    carControls[carName].speedGauge.setValue(data[0]);
    if (data[3] > 0) {
        // how many time is left
        var percentage = data[3] / (settings[carName].refillTime / 100);
        // how much is fueld up
        var value = settings[carName].fuelFull - ((settings[carName].fuelFull / 100) * percentage)
        carControls[carName].fuelGauge.setValue(value);
    } else {
        carControls[carName].fuelGauge.setValue(data[2]);
    }



    var lapTime = Number(lapData[0]);
    carControls[carName].laptimer.setValue(lapTime / 1000);

    // car ended a lap
    if (lapData[1] == 1) {
        if (carControls[carName].fastesLap == null || carControls[carName].fastesLap > lapTime) {
            carControls[carName].fastesLap = lapTime;
            carControls[carName].fastesLaptimer.setValue(carControls[carName].fastesLap / 1000);
        }

        carControls[carName].lastLaptimer.setValue(lapTime  / 1000);
    }
}

/**
 * is called when we receive data over websocket
 * @param data
 */
var onMessage = function (data) {
    var data = data.split(',');
    if (data.length == 14) {

        if (data[0] == -1) {
            startLight1.setLedOnOff(false);
            startLight2.setLedOnOff(false);
            startLight3.setLedOnOff(false);
            startLight4.setLedOnOff(false);
        }

        if (data[0] == 1) {
            startLight1.setLedOnOff(true);
        }
        if (data[0] == 2) {
            startLight2.setLedOnOff(true);
        }
        if (data[0] == 3) {
            startLight3.setLedOnOff(true);
        }
        if (data[0] == 4) {
            startLight4.setLedOnOff(true);
        }

        if (data[1] == 1) {
            powerLight.setLedColor(steelseries.LedColor.GREEN_LED);
            powerLight.blink(false);
            powerLight.setLedOnOff(true);
        } else {
            powerLight.setLedColor(steelseries.LedColor.RED_LED);
            powerLight.blink(true);
        }

        updateCarControlls(1, data.slice(2, 6), data.slice(10, 12));
        updateCarControlls(2, data.slice(6, 10), data.slice(12, 14));
    }
}