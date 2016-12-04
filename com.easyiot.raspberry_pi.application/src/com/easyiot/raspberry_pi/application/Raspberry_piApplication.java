package com.easyiot.raspberry_pi.application;

import java.lang.reflect.InvocationTargetException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyiot.base.api.Device;
import com.easyiot.base.api.Device.DeviceExecutorMethodTypeEnum;
import com.easyiot.base.api.exception.NoSuchDeviceException;
import com.easyiot.base.capability.DeviceRest.RequireDeviceRest;
import com.easyiot.base.executor.DeviceExecutorService;
import com.easyiot.color3led.device.api.dto.ColorDtoFactory;
import com.easyiot.raspberry_pi.application.config.DiscoballConfig;

import osgi.enroute.configurer.api.RequireConfigurerExtender;

@RequireDeviceRest
@RequireConfigurerExtender
@Component(name = "com.easyiot.raspberry_pi", configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = DiscoballConfig.class)
public class Raspberry_piApplication {
	@Reference
	private DeviceExecutorService rm;

	// Get the three color device
	@Reference(target = "(id=discoDevice.1)")
	private Device _threeColorDevice;

	private DiscoballConfig config;
	private static final Logger s_logger = LoggerFactory.getLogger(Raspberry_piApplication.class);
	private volatile boolean runThread = true;

	@Activate
	public void activate(DiscoballConfig config) {
		this.config = config;
		new DiscoThread().start();
	}

	@Modified
	public void modified(DiscoballConfig config)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		this.config = config;
		switch (config.start()) {
		case START:
			runThread = true;
			new DiscoThread().start();
			break;
		case STOP:
			runThread = false;
			turnOffLedsDisco();
			s_logger.info("stopping now");
			break;
		}
	}

	@Deactivate
	public void deactivate()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		// Only stop the thread if this is a proper de-activation due to stopping
		// the bundle but not due to unsuccessful binding
		if (_threeColorDevice != null) {
			runThread = false;
		}
		turnOffLedsDisco();
		s_logger.info("deactivated.");
	}

	private void turnOffLedsDisco() throws NoSuchMethodException {
		// Ignore if resource manager cannot find the device to run
		try {
			rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
					DeviceExecutorMethodTypeEnum.POST);
			rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
					DeviceExecutorMethodTypeEnum.POST);
			rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
					DeviceExecutorMethodTypeEnum.POST);
		} catch (NoSuchDeviceException | NullPointerException e) {
			// Do nothing
			e.printStackTrace();
		}
	}

	private class DiscoThread extends Thread {
		@Override
		public void run() {
			int colorScheme = 0;
			while (runThread) {
				// disco ball!!!!

				try {
					updateColor(colorScheme);
					Thread.sleep(config.frequency());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | InterruptedException | NoSuchDeviceException e) {
					// Do nothing
					e.printStackTrace();
				}

				colorScheme += 1;
				if (colorScheme % 3 == 0) {
					colorScheme = 0;
				}
			}
		}

		private void updateColor(int colorScheme) throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchMethodException, InterruptedException {
			switch (colorScheme) {
			case 0:
				rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.RED_COLOR, Void.TYPE,
						DeviceExecutorMethodTypeEnum.POST);
				break;
			case 1:
				rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.GREEN_COLOR, Void.TYPE,
						DeviceExecutorMethodTypeEnum.POST);
				break;
			case 2:
				rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.BLUE_COLOR, Void.TYPE,
						DeviceExecutorMethodTypeEnum.POST);
				break;
			}
		}
	}
}
