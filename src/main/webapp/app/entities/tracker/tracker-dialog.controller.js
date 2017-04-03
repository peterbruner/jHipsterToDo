(function() {
    'use strict';

    angular
        .module('jHipsterToDoApp')
        .controller('TrackerDialogController', TrackerDialogController);

    TrackerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tracker'];

    function TrackerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Tracker) {
        var vm = this;

        vm.tracker = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tracker.id !== null) {
                Tracker.update(vm.tracker, onSaveSuccess, onSaveError);
            } else {
                Tracker.save(vm.tracker, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jHipsterToDoApp:trackerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
