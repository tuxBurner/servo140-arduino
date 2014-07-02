$(function () {

    var powerControll = powerLight();
    powerControll.setup();

    var lightsControll = startLights();
    lightsControll.setup();

    var raceCarsControll = raceCars(jsonRace.laps);

    var settingsModals = settingsModal();

    var com = communications();
    com.setupGui(function (carSettings) {
        settingsModals.setSettings(carSettings);
    });

    com.setPowerControll(powerControll.onData);
    com.setLightControll(lightsControll.onData);
    com.setUpdateCarControlls(raceCarsControll.onData);
    com.start();


    if (powerControll.currentPowerState == 1) {
        com.powerOff();
    }

    $('#racePowerOffBtn').click(function () {
        com.powerOff();
    });

    $('#raceLightBtn').click(function () {
        com.startLightSeq();
    });

});