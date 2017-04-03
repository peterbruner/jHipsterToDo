(function() {
    'use strict';

    angular
        .module('jHipsterToDoApp')
        .controller('TrackerDetailController', TrackerDetailController);

    TrackerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Tracker'];

    function TrackerDetailController($scope, $rootScope, $stateParams, previousState, entity, Tracker) {
        var vm = this;

        vm.tracker = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jHipsterToDoApp:trackerUpdate', function(event, result) {
            vm.tracker = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
