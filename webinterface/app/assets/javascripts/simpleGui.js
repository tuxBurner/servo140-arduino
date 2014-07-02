$(function () {
    var powerControll = powerLight();
    powerControll.setup();

    var lightsControll = startLights();
    lightsControll.setup();

    var raceCarsControll = simpleCars();

    var settingsModals = settingsModal();

    var com = communications();
    com.setupGui(function (carSettings) {
        raceCarsControll.setup(carSettings);
        settingsModals.setSettings(carSettings);
    });


    com.setPowerControll(powerControll.onData);
    com.setLightControll(lightsControll.onData);
    com.setUpdateCarControlls(raceCarsControll.onData);
    com.start();


    $('#powerOffBtn').click(function () {
        com.powerOff();
    });

    $('#lightBtn').click(function () {
        com.startLightSeq();
    });

});