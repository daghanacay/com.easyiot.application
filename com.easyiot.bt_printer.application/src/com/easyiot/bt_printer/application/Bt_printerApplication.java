package com.easyiot.bt_printer.application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.easyiot.base.api.Device;
import com.easyiot.base.api.Device.DeviceExecutorMethodTypeEnum;
import com.easyiot.base.api.exception.NoSuchDeviceException;
import com.easyiot.base.executor.DeviceExecutorService;
import com.easyiot.com.easyiot.ZJ_580.bt_printer.device.api.dto.ZJ_580Data;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.debug.api.Debug;

@RequireConfigurerExtender
@Component(service = Bt_printerApplication.class, name = "com.easyiot.bt_printer", immediate = true, property = {
		Debug.COMMAND_SCOPE + "=printer", Debug.COMMAND_FUNCTION + "=write" })
public class Bt_printerApplication {

	@Reference
	private DeviceExecutorService rm;
	// Get the three color device
	@Reference(target = "(id=ZJ_580.1)")
	private Device printerDevice;

	public void write(String msg) throws NoSuchDeviceException, NoSuchMethodException {
		ZJ_580Data data = new ZJ_580Data();
		data.message = msg;
		rm.activateResource(printerDevice.getId(), data, String.class, DeviceExecutorMethodTypeEnum.POST);
	}

}
