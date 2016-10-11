package com.easyiot.heatmap.application;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.easyiot.base.api.Device;
import com.easyiot.base.api.Device.DeviceExecutorMethodTypeEnum;
import com.easyiot.base.capability.DeviceRest.RequireDeviceRest;
import com.easyiot.base.capability.WebSecurity.RequireWebSecurity;
import com.easyiot.base.executor.DeviceExecutorService;
import com.easyiot.heatmap.application.dto.AppSensorDataDTO;
import com.easyiot.heatmap.application.dto.converter.AusloraSensorConverter;
import com.easyiot.heatmap.application.dto.converter.LoraSensorDataConverter;
import com.easyiot.lora.device.api.dto.SensorDataDTO;

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

	private LoraSensorDataConverter converter = new LoraSensorDataConverter();
	private AusloraSensorConverter converter2 = new AusloraSensorConverter();

	public List<AppSensorDataDTO> getSensorData()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		List<AppSensorDataDTO> returnVal = new ArrayList<>();
		SensorDataDTO loraSensorData;
		for (Device loraSensor : loraSensors) {
			loraSensorData = rm.activateResource(loraSensor.getId(), null, SensorDataDTO.class,
					DeviceExecutorMethodTypeEnum.GET);
			returnVal.add(converter.convert(loraSensor.getId(), loraSensorData));
		}
		com.easyiot.auslora.device.api.dto.SensorDataDTO ausloraSensorData;
		for (Device ausloraSensor : ausloraSensors) {
			ausloraSensorData = rm.activateResource(ausloraSensor.getId(), null,
					com.easyiot.auslora.device.api.dto.SensorDataDTO.class, DeviceExecutorMethodTypeEnum.GET);
			returnVal.add(converter2.convert(ausloraSensor.getId(), ausloraSensorData));
		}

		return returnVal;
	}

}
