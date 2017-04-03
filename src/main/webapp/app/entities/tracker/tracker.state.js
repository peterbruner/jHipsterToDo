(function() {
    'use strict';

    angular
        .module('jHipsterToDoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tracker', {
            parent: 'entity',
            url: '/tracker',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Trackers'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tracker/trackers.html',
                    controller: 'TrackerController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('tracker-detail', {
            parent: 'tracker',
            url: '/tracker/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Tracker'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tracker/tracker-detail.html',
                    controller: 'TrackerDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Tracker', function($stateParams, Tracker) {
                    return Tracker.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tracker',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tracker-detail.edit', {
            parent: 'tracker-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracker/tracker-dialog.html',
                    controller: 'TrackerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tracker', function(Tracker) {
                            return Tracker.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tracker.new', {
            parent: 'tracker',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracker/tracker-dialog.html',
                    controller: 'TrackerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                item: null,
                                location: null,
                                isComplete: false,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tracker', null, { reload: 'tracker' });
                }, function() {
                    $state.go('tracker');
                });
            }]
        })
        .state('tracker.edit', {
            parent: 'tracker',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracker/tracker-dialog.html',
                    controller: 'TrackerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tracker', function(Tracker) {
                            return Tracker.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tracker', null, { reload: 'tracker' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tracker.delete', {
            parent: 'tracker',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tracker/tracker-delete-dialog.html',
                    controller: 'TrackerDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tracker', function(Tracker) {
                            return Tracker.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tracker', null, { reload: 'tracker' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
