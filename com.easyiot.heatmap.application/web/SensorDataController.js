app.controller("SensorDataController", [ '$scope', function($scope) {
    $scope.$parent.mapHeight = "200px";
    // Function to center the map on the selected sensor and create marker at
    // the selected location
    $scope.centerSensor = function(sensorData) {
	$scope.center.lat = sensorData.latitude;
	$scope.center.lng = sensorData.longitude;

	$scope.$parent.markers = {
	    m1 : {
		lat : sensorData.latitude,
		lng : sensorData.longitude,
		message : sensorData.metadata.notes
	    }
	};

    }
    // Ordering the table based on the Header values
    $scope.orderByHeader = function(orderByStr) {
	$scope.orderByStr = orderByStr;
	$scope.isReverse = !$scope.isReverse;
    };
} ]);