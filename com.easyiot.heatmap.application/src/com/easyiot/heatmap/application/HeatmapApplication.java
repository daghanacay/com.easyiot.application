package com.easyiot.heatmap.application;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.easyiot.LT100H.device.api.dto.LT100HSensorDataDTO;
import com.easyiot.base.api.Device;
import com.easyiot.base.api.Device.DeviceExecutorMethodTypeEnum;
import com.easyiot.base.capability.DeviceRest.RequireDeviceRest;
import com.easyiot.base.capability.WebSecurity.RequireWebSecurity;
import com.easyiot.base.executor.DeviceExecutorService;
import com.easyiot.development.board1.device.api.dto.DevelopmentBoard1DeviceDataDTO;
import com.easyiot.heatmap.application.dto.AppSensorDataDTO;
import com.easyiot.heatmap.application.dto.converter.DevelopmentBoardSensorDataConverter;
import com.easyiot.heatmap.application.dto.converter.LT100HSensorConverter;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireDeviceRest(versionStr = "1.0.0")
@RequireWebSecurity(versionStr = "1.0.0")
@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "com.daghan.heatmap")
public class HeatmapApplication implements REST {
	@Reference
	private DeviceExecutorService rm;

	// List of lora sensors see configuration/configuration.json
	@Reference(target = "(service.factoryPid=com.easyiot.device.lora.device)")
	volatile List<Device> loraSensors;

	@Reference(target = "(service.factoryPid=com.easyiot.auslora.device)")
	volatile List<Device> ausloraSensors;

	private DevelopmentBoardSensorDataConverter converter = new DevelopmentBoardSensorDataConverter();
	private LT100HSensorConverter converter2 = new LT100HSensorConverter();

	public List<AppSensorDataDTO> getSensorData()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		List<AppSensorDataDTO> returnVal = new ArrayList<>();
		DevelopmentBoard1DeviceDataDTO devBoardSensorData;
		for (Device loraSensor : loraSensors) {
			devBoardSensorData = rm.activateResource(loraSensor.getId(), null, DevelopmentBoard1DeviceDataDTO.class,
					DeviceExecutorMethodTypeEnum.GET);
			returnVal.add(converter.convert(loraSensor.getId(), devBoardSensorData));
		}
		LT100HSensorDataDTO lt100hSensorData;
		for (Device ausloraSensor : ausloraSensors) {
			lt100hSensorData = rm.activateResource(ausloraSensor.getId(), null, LT100HSensorDataDTO.class,
					DeviceExecutorMethodTypeEnum.GET);
			returnVal.add(converter2.convert(ausloraSensor.getId(), lt100hSensorData));
		}

		return returnVal;
	}

}
