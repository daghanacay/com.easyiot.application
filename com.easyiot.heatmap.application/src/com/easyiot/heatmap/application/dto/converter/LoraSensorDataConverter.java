package com.easyiot.heatmap.application.dto.converter;

import com.easyiot.heatmap.application.dto.AppSensorDataDTO;
import com.easyiot.lora.device.api.dto.SensorDataDTO;

public class LoraSensorDataConverter {
	// Converts the data coming from sensor to application DTO that will be sent
	// to front end. Does not clone the values so input value should be
	// immutable.
	public AppSensorDataDTO convert(String name, SensorDataDTO sensorDto) {
		AppSensorDataDTO returnval = new AppSensorDataDTO();
		returnval.name = name;
		returnval.longitude = sensorDto.lon;
		returnval.latitude = sensorDto.lat;
		returnval.temperature = sensorDto.temp;
		returnval.sensorNormalized = normalizeData(returnval.temperature);
		
		return returnval;
	}

	/**
	 * Normalizes temperature data between -20 and +100
	 * 
	 * @param temperature
	 * @return
	 */
	private double normalizeData(double temperature) {
		return (temperature + 20) / 120;
	}
	
}
