package com.easyiot.heatmap.application.dto.converter;

import com.easyiot.development.board1.device.api.dto.DevelopmentBoard1DeviceDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorMetadataDTO;

public class DevelopmentBoardSensorDataConverter {
	// Converts the data coming from sensor to application DTO that will be sent
	// to front end. Does not clone the values so input value should be
	// immutable.
	public AppSensorDataDTO convert(String name, DevelopmentBoard1DeviceDataDTO sensorDto) {
		AppSensorDataDTO returnval = new AppSensorDataDTO();
		returnval.name = name;
		returnval.longitude = sensorDto.lon;
		returnval.latitude = sensorDto.lat;
		returnval.temperature = sensorDto.temp;
		returnval.sensorNormalized = normalizeData(returnval.temperature);
		returnval.metadata = createMetaData(sensorDto);

		return returnval;
	}

	private AppSensorMetadataDTO createMetaData(DevelopmentBoard1DeviceDataDTO sensorDto) {
		AppSensorMetadataDTO returnVal = new AppSensorMetadataDTO();
		returnVal.notes = String.format("Network name: %s, Gateway EUI: %s", sensorDto.metadata.networkName,
				sensorDto.metadata.gateway_eui);
		return returnVal;
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
