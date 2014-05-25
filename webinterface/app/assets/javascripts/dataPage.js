$(function () {

    $('#dataModal').modal({ show: false});

    // bind event to the form add button
    $(document).on('click','#dataModalSaveBtn', function() {
      $('#dataAddForm').submit();
    });


    // bind events to the add buttons
    $(document).on('click','#driverAddBtn', function() {
        openAddModal(jsRoutes.controllers.DataController.displayAddDriver());
    });




    loadDriverTab();
})

var loadDriverTab = function () {
    pAjax(jsRoutes.controllers.DataController.listDrivers(), null, function (data) {
        $('#drivers').html(data);
    }, function (data) {
    });
}


var openAddModal = function(route) {
    pAjax(route, null, function (data) {
        $('#dataModalContent').html(data);
        $('#dataModal').modal('show');
    }, function (data) {
    });
}