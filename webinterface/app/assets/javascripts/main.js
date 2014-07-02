/**
 * This function handles an ajax call to an javascript route which is configured
 */
function pAjax(controller, fnData, sucessFn, errorFn) {
    controller.ajax({
        data: fnData,
        success: function (data) {
            sucessFn(data);
        },
        error: function (data) {
            // TODO: on error and no errorFn alert here something
            errorFn(data)
        }
    });
}