package com.easyiot.heatmap.application.dto.converter;

import com.easyiot.gps_rpi.device.api.dto.GpsRpiDeviceDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorMetadataDTO;

public class GpsRpiHSensorConverter {
	public AppSensorDataDTO convert(String name, GpsRpiDeviceDataDTO gpsRpiSensorData) {
		AppSensorDataDTO returnval = new AppSensorDataDTO();

		returnval.name = name;
		returnval.longitude = Double.valueOf(gpsRpiSensorData.longitude);
		returnval.latitude = Double.valueOf(gpsRpiSensorData.latitude);

		// No heat info on this sensor

		returnval.temperature = 50;
		returnval.sensorNormalized = .5;
		returnval.metadata = createMetaData(gpsRpiSensorData);
		return returnval;
	}

	private AppSensorMetadataDTO createMetaData(GpsRpiDeviceDataDTO sensorDto) {
		AppSensorMetadataDTO returnVal = new AppSensorMetadataDTO();
		returnVal.notes = String.format("Network name: %s, Gateway EUI: %s", sensorDto.metadata!=null?sensorDto.metadata.networkName:"N/A",
				sensorDto.metadata!=null?sensorDto.metadata.gateway_eui:"N/A");
		return returnVal;
	}
}
