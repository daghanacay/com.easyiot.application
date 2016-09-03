var app = angular.module("heatMapApp", [ 'ui-leaflet', 'ngRoute' ]);
app
	.controller(
		"MainHeatmapController",
		[
			"$scope",
			"$http",
			"$interval",
			function($scope, $http, $interval) {
			    $scope.$on('$routeChangeStart', function(event,
				    next, current) {
				$scope.mapHeight = "950px";
			    });

			    $interval(function() {
				updateData();
				$scope.layers.overlays.heat.doRefresh = true;
			    }, 10000);

			    updateData = function() {
				$http.get("../rest/sensorData").success(
					function(data) {
					    // Store the data
					    $scope.sensorData = data;
					    // Convert sensor data to be used by
					    // the heat map
					    mapData = data.map(function(dat) {
						return [ dat.latitude,
							dat.longitude,
							dat.sensorNormalized ]
					    });
					    $scope.layers.overlays = {
						heat : {
						    name : 'Heat Map',
						    type : 'heat',
						    data : mapData,
						    layerOptions : {
							radius : 20,
							blur : 10
						    },
						    visible : true,
						    doRefresh : true
						}
					    };
					});
			    }

			    angular
				    .extend(
					    $scope,
					    {
						center : {
						    lat : -37.8140000,
						    lng : 144.9633200,
						    zoom : 16
						},
						markers : {},
						layers : {
						    baselayers : {
							osm : {
							    name : 'OpenStreetMap',
							    url : 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
							    type : 'xyz'
							}
						    }
						}
					    });
			} ]);

// Configuating navigation
app.config([ "$routeProvider", function($routeProvider) {
    $routeProvider.when("/sensorDataView", {
	templateUrl : "/heatmap/views/SensorDataView.html",
	controller : "SensorDataController"
    }).when("/warningsView", {
	templateUrl : "/heatmap/views/WarningsView.html",
	controller : ""
    });
} ]);
