var websocket = null;

var car1SpeedGauge,
    car1FuelGauge,
    car2SpeedGauge,
    car2FuelGauge,
    startLight1,
    startLight2,
    startLight3,
    startLight4,
    powerLight;

var gradColors = [ new steelseries.rgbaColor(0, 0, 200, 1),
    new steelseries.rgbaColor(0, 200, 0, 1),
    new steelseries.rgbaColor(200, 200, 0, 1),
    new steelseries.rgbaColor(200, 0, 0, 1),
    new steelseries.rgbaColor(200, 0, 0, 1) ];

var gradSections = [ 0, 0.33, 0.66, 0.85, 1];

var powerGrad = new steelseries.gradientWrapper(0, 250, gradSections, gradColors);
var fuelGrad = new steelseries.gradientWrapper(0, 10000, gradSections, gradColors);


$(function () {
    var wsUri = jsRoutes.controllers.ApplicationController.joinRoomWs().webSocketURL();

    websocket = new WebSocket(wsUri);
    websocket.onopen = function (evt) {
    };
    websocket.onclose = function (evt) {
    };
    websocket.onmessage = function (evt) {
        onMessage(evt)
    };
    websocket.onerror = function (evt) {
        onError(evt)
    };

    function onError(evt) {
        websocket.close();
    }

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


    // car1 stuff
    car1SpeedGauge = new steelseries.RadialBargraph('car1SpeedCanvas', {
        gaugeType: steelseries.GaugeType.TYPE4,
        size: 201,
        titleString: 'Car A',
        unitString: 'Power',
        valueGradient: powerGrad,
        useValueGradient: true,
        threshold: 50,
        lcdVisible: true,
        minValue: 0,
        maxValue: 250
    });

    car1FuelGauge = new steelseries.LinearBargraph('car1FuelCanvas', {
        width: 320,
        height: 140,
        valueGradient: fuelGrad,
        useValueGradient: true,
        titleString: "Car A",
        unitString: "Fuel",
        threshold: 1000,
        lcdVisible: true,
        minValue: 0,
        maxValue: 10000
    });

    // car2 stuff
    car2SpeedGauge = new steelseries.RadialBargraph('car2SpeedCanvas', {
        gaugeType: steelseries.GaugeType.TYPE4,
        size: 201,
        titleString: 'Car B',
        unitString: 'Power',
        valueGradient: powerGrad,
        useValueGradient: true,
        threshold: 50,
        lcdVisible: true,
        minValue: 0,
        maxValue: 250
    });

    car2FuelGauge = new steelseries.LinearBargraph('car2FuelCanvas', {
        width: 320,
        height: 140,
        valueGradient: fuelGrad,
        useValueGradient: true,
        titleString: "Car B",
        unitString: "Fuel",
        threshold: 1000,
        lcdVisible: true,
        minValue: 0,
        maxValue: 10000
    });
});

function onMessage(evt) {
    var data = evt.data.split(',');
    if (data.length == 10) {

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

        if(data[1] == 1) {
            powerLight.setLedColor(steelseries.LedColor.GREEN_LED);
            powerLight.blink(false);
            powerLight.setLedOnOff(true);
        } else {
            powerLight.setLedColor(steelseries.LedColor.RED_LED);
            powerLight.blink(true);
        }


        car1SpeedGauge.setValue(data[2]);
        car1FuelGauge.setValue(data[4]);
        if(data[5] > 0) {
            // how many time is left
            var percentage = data[5] / (10000 / 100) ;

            // how much is fueld up
            var value = 10000 - ((10000 / 100) * percentage)
            car1FuelGauge.setValue(value);
        }

        car2SpeedGauge.setValue(data[6]);
        car2FuelGauge.setValue(data[8]);
        if(data[9] > 0) {
            // how many time is left
            var percentage = data[9] / (10000 / 100) ;

            // how much is fueld up
            var value = 10000 - ((10000 / 100) * percentage)
            car2FuelGauge.setValue(value);
        }
    }
}