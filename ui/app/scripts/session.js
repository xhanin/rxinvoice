'use strict';

angular
    .module('rxSession', [
        'ngResource',
        'angular-md5'
    ])
    .factory('Sessions', function($resource) {
        return $resource('/api/sessions/:sessionKey', {sessionKey: 'current'});
    })
    .factory('SecurityHttpInterceptor', function($q, $location) {
        return function(promise) {
            return promise.then(function(response) {
                return response;
            }, function(response) {
                if (response.status == 401 || response.status == 403) {
                    var backTo = encodeURIComponent(location);
                    $location.path('/login');
                }
                return $q.reject(response);
            });
        }
    })
    .controller('SessionController', function($scope, Sessions) {
        $scope.session = Sessions.get()

        $scope.logout = function() {
            Sessions.delete(function() { location.reload() });
        }
    })
    .config(function($httpProvider) {
        $httpProvider.responseInterceptors.push('SecurityHttpInterceptor');
    })
    .controller('LoginCtrl', function ($scope, $http, $location, md5) {
        $scope.authenticate = function(username, password) {
            $http.post('/api/sessions', {principal: {name: username, passwordHash: md5.createHash(password)}})
                .success(function(data, status, headers, config) {
                    console.log('authenticated', data, status);
                    $location.path('/');
                }).error(function(data, status, headers, config) {
                    console.log('error', data, status);
                    alert("Authentication error, please try again.");
                });
        }
    })
;