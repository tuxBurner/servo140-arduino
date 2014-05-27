$(function () {

    $('#dataModal').modal({ show: false});

    // bind event to the form add button
    $(document).on('click', '#dataModalSaveBtn', function () {
        $('#dataAddForm').submit();
    });

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        var hash = e.target.hash;
        $(hash).html("");
        e.target // activated tab
        loadDdataTab(hash.substring(1));

    })

    // bind events to the modal
    $('#dataModal').on('shown.bs.modal', function (e) {
        startWebcamCapture();
    })

    $('#dataModal').on('hidden.bs.modal', function (e) {
        Webcam.reset();
    })

    $(document).on('click', '.dataAddBtn', function () {
        openAddModal(jsRoutes.controllers.DataController.displayAddForm($(this).data('formtype')));
    });

    // driver buttons
    $(document).on('click', '.driverEditBtn', function () {
        openAddModal(jsRoutes.controllers.DataController.displayEditDriver($(this).data('driverid')));
    });
    // car buttons
    $(document).on('click', '.carEditBtn', function () {
        openAddModal(jsRoutes.controllers.DataController.displayEditCar($(this).data('carid')));
    });

    // tracks buttons
    $(document).on('click', '.trackEditBtn', function () {
        openAddModal(jsRoutes.controllers.DataController.displayEditTrack($(this).data('trackid')));
    });


    loadDdataTab(currentTab);
    $('#dataTab a[href="#' + currentTab+ '"]').tab('show');
})

/**
 * Loads the data in the tab
 * @param hash
 */
var loadDdataTab = function (currentTab) {

    var url = "";

    switch (currentTab) {
        case "drivers":
            url = jsRoutes.controllers.DataController.listDrivers();
            break;
        case "cars" :
            url = jsRoutes.controllers.DataController.listCars();
            break;
        case "tracks" :
            url = jsRoutes.controllers.DataController.listTracks();
            break;
    }

    pAjax(url, null, function (data) {
        $('#'+currentTab).html(data);
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
    initializeFileDrop();
}

/**
 * Takes a snapshot from the webcam
 */
var takeSnapshot = function () {
    var data_uri = Webcam.snap();
    updateImageData(data_uri);
}

/**
 * Updates the preview image and sets the data to the input field
 * @param imgData
 */
var updateImageData = function (imgData) {
    $('#my_result').html('<img src="' + imgData + '"/>');
    var raw_image_data = imgData.replace(/^data\:image\/\w+\;base64\,/, '');
    $('#imageData').val(raw_image_data);
}

/**
 * Initializes the drop zone for the image
 */
var initializeFileDrop = function () {
    // Setup the dnd listeners.
    var dropZone = document.getElementById('dropZone');
    dropZone.addEventListener('dragover', handleDragOver, false);
    dropZone.addEventListener('drop', handleFileSelect, false);
}

/**
 * Handles the event when the user drags the file over the zone
 * @param evt
 */
function handleDragOver(evt) {
    evt.stopPropagation();
    evt.preventDefault();
    evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
}

/**
 * Is called when the file is dropped into the zone
 * @param evt
 */
function handleFileSelect(evt) {
    evt.stopPropagation();
    evt.preventDefault();

    var file = evt.dataTransfer.files[0];
    if (file.type.match('image.*')) {
        var reader = new FileReader();
        // Closure to capture the file information.
        reader.onload = (function (theFile) {
            return function (e) {
                updateImageData(e.target.result);
            };
        })(file);

        // Read in the image file as a data URL.
        reader.readAsDataURL(file);
    }
}

var startWebcam = function () {
    $("#webcamControl").show(function () {
        Webcam.set({
            image_format: 'jpeg',
            jpeg_quality: 90,
            force_flash: false
        });
        Webcam.attach('#my_camera');
    });
}