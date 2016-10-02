package com.easyiot.heatmap.application.dto.converter;

import com.easyiot.auslora.device.api.dto.AusloraDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorDataDTO;
import com.easyiot.lora.device.api.dto.MetaDataDTO;
import com.easyiot.lora.device.api.dto.SensorDataDTO;

public class LoraSensorDataConverter {
	// Converts the data coming from sensor to application DTO that will be sent
	// to front end. Does not clone the values so input value should be
	// immutable.
	public AppSensorDataDTO convert(String name, SensorDataDTO sensorDto) {
		AppSensorDataDTO returnval = new AppSensorDataDTO();
		returnval.name = name;
		returnval.longitude = sensorDto.metadata.get(0).longitude;
		returnval.latitude = sensorDto.metadata.get(0).latitude;
		returnval.temperature = Double.valueOf(sensorDto.payload);
		returnval.sensorNormalized = normalizeData(returnval.temperature);
		returnval.metadata.notes = createNotes(sensorDto.metadata.get(0));
		return returnval;
	}

	/**
	 * Converts the metadata of the sensor information
	 * 
	 * @param sensorMetadata
	 * @return
	 */
	private String createNotes(MetaDataDTO sensorMetadata) {
		return sensorMetadata.toString();
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
