package com.easyiot.heatmap.application;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import com.easyiot.LT100H.device.api.dto.LT100HSensorDataDTO;
import com.easyiot.base.api.Device;
import com.easyiot.base.api.Device.DeviceExecutorMethodTypeEnum;
import com.easyiot.base.capability.ConfigurationManagement.RequireConfigurationManagement;
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
@RequireConfigurationManagement
@Component(name = "com.easyiot.heatmap")
public class HeatmapApplication implements REST {
	@Reference
	private DeviceExecutorService rm;

	@Reference
	private LogService logService;

	// List of lora sensors see configuration/configuration.json
	@Reference(target = "(service.factoryPid=com.easyiot.development.board1.device)")
	volatile List<Device> devBoardSensors;

	@Reference(target = "(service.factoryPid=com.easyiot.LT100H.device)")
	volatile List<Device> lt100hSensors;

	private DevelopmentBoardSensorDataConverter converter = new DevelopmentBoardSensorDataConverter();
	private LT100HSensorConverter converter2 = new LT100HSensorConverter();

	public List<AppSensorDataDTO> getSensorData()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		List<AppSensorDataDTO> returnVal = new ArrayList<>();

		DevelopmentBoard1DeviceDataDTO devBoardSensorData;
		for (Device devBoardSensor : devBoardSensors) {
			devBoardSensorData = rm.activateResource(devBoardSensor.getId(), null, DevelopmentBoard1DeviceDataDTO.class,
					DeviceExecutorMethodTypeEnum.GET);
			returnVal.add(converter.convert(devBoardSensor.getId(), devBoardSensorData));
		}

		LT100HSensorDataDTO lt100hSensorData;
		for (Device lt100hSensor : lt100hSensors) {
			lt100hSensorData = rm.activateResource(lt100hSensor.getId(), null, LT100HSensorDataDTO.class,
					DeviceExecutorMethodTypeEnum.GET);
			returnVal.add(converter2.convert(lt100hSensor.getId(), lt100hSensorData));
		}
		logService.log(LogService.LOG_DEBUG, "Return from sensor data.");
		return returnVal;
	}

}
