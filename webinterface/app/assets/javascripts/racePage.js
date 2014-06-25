/**
 * Handles all the race magic stuff
 */

var raceSetting = null;

/**
 * Opens the race settings dialo where the race mode is set and the drivers and cars and track is selected
 */
var displayRaceSettingsDialog = function() {
    $(raceSettingsData.drivers).each(function(i,obj) {
        $('#test').append('<option value="'+obj.id+'" data-content="<img height=\'24\' src=\'/data/image/'+obj.id+'\' /> '+obj.name+'">'+obj.name+'</option>')
    });

    $('#test').selectpicker();
    $('#raceSettingsModal').modal('show');
}

$(function() {
  displayRaceSettingsDialog();
});