package com.easyiot.heatmap.application.dto.converter;

import com.easyiot.auslora.device.api.dto.AusloraDataDTO;
import com.easyiot.auslora.device.api.dto.sensor.SensorDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorDataDTO;

public class AusloraSensorConverter {
	public AppSensorDataDTO convert(String name, AusloraDataDTO ausloraSensorDevice) {
		AppSensorDataDTO returnval = new AppSensorDataDTO();
		SensorDataDTO sensorData = ausloraSensorDevice.sensorData;
		returnval.name = name;
		returnval.longitude = getLongitude(sensorData.longitude);
		returnval.latitude = getLatitude(sensorData.latitude);
		
		// No heat info on this sensor
		returnval.temperature = 50;
		returnval.sensorNormalized = .5;
		return returnval;
	}

	private double getLatitude(String latitude) {
		// Convert (N or S)ddmm.mmmm
		double returnVal = 0;
		String direction = latitude.substring(0, 1);
		double latitudeDbl = Double.valueOf(latitude.substring(1)) / 100;
		if ("N".equalsIgnoreCase(direction)) {
			returnVal = latitudeDbl;
		} else if ("S".equalsIgnoreCase(direction)) {
			returnVal = -latitudeDbl;
		} else {
			System.err.println("corrupted data");
		}

		return returnVal;
	}

	private double getLongitude(String longitude) {
		// Convert // in (E or W)dddmm.mmmm format to double
		double returnVal = 0;
		String direction = longitude.substring(0, 1);
		double longitudeDbl = Double.valueOf(longitude.substring(1)) / 100;
		if ("E".equalsIgnoreCase(direction)) {
			returnVal = longitudeDbl;
		} else if ("W".equalsIgnoreCase(direction)) {
			returnVal = -longitudeDbl;
		} else {
			System.err.println("corrupted data");
		}

		return returnVal;
	}

}
