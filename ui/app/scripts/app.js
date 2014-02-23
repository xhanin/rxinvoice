'use strict';

angular.module('rxinvoiceApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'rxSession',
  'invoice'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/login', {
        templateUrl: 'views/login.html'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
