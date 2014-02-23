'use strict';

angular
    .module('invoice', [
        'ngResource'
    ])
    .factory('Invoices', function($resource) {
        return $resource('/api/invoices/:key');
    })
    .controller('InvoiceCtrl', function($scope, Invoices) {
        $scope.invoices = Invoices.query();
    })
;