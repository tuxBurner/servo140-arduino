$(function () {

    $('#dataModal').modal({ show: false});

    // bind event to the form add button
    $(document).on('click', '#dataModalSaveBtn', function () {
        $('#dataAddForm').submit();
    });

    // bind events to the modal
    $('#dataModal').on('shown.bs.modal', function (e) {
        startWebcamCapture();
    })

    $('#dataModal').on('hidden.bs.modal', function (e) {
        Webcam.reset();
    })


    // bind events to the add buttons
    $(document).on('click', '#driverAddBtn', function () {
        openAddModal(jsRoutes.controllers.DataController.displayAddDriver());
    });
    $(document).on('click', '.driverEditBtn', function () {
        openAddModal(jsRoutes.controllers.DataController.displayEditDriver($(this).data('driverid')));
    });

    loadDriverTab();
})

var loadDriverTab = function () {
    pAjax(jsRoutes.controllers.DataController.listDrivers(), null, function (data) {
        $('#drivers').html(data);
    }, function (data) {
    });
}


/**
 * Opens a modal
 * @param route
 */
var openAddModal = function (route) {
    pAjax(route, null, function (data) {
        $('#dataModalContent').html(data);
        $('#dataModal').modal('show');
    }, function (data) {
    });
}

/**
 * Starts the webcam capture
 */
var startWebcamCapture = function () {

    $('#my_result').html('<img src="data:image/jpeg;base64,' + $('#imageData').val() + '"/>');

    Webcam.set({
        image_format: 'jpeg',
        jpeg_quality: 90,
        force_flash: false
    });
    Webcam.attach('#my_camera');
}

/**
 * Takes a snapshot from the webcam
 */
var takeSnapshot = function () {
    var data_uri = Webcam.snap();
    $('#my_result').html('<img src="' + data_uri + '"/>');
    var raw_image_data = data_uri.replace(/^data\:image\/\w+\;base64\,/, '');
    $('#imageData').val(raw_image_data);
}