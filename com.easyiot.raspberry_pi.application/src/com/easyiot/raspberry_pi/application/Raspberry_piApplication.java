package com.easyiot.raspberry_pi.application;

import java.lang.reflect.InvocationTargetException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyiot.base.api.Device;
import com.easyiot.base.api.Device.DeviceExecutorMethodTypeEnum;
import com.easyiot.base.capability.DeviceRest.RequireDeviceRest;
import com.easyiot.base.executor.DeviceExecutorService;
import com.easyiot.color3led.device.api.capability.Color3LedCapability.RequireColor3LedDevice;
import com.easyiot.color3led.device.api.dto.ColorDtoFactory;
import com.easyiot.raspberry_pi.application.Raspberry_piApplication.StartStopEnum;

import osgi.enroute.configurer.api.RequireConfigurerExtender;

@ObjectClassDefinition(name = "Disco ball configuration")
@interface DiscoballConfig {
	@AttributeDefinition(name = "Stop/Start", description = "Stops or start the disco ball.")
	StartStopEnum start() default StartStopEnum.START;

	@AttributeDefinition(name = "Frequency", description = "Frequency in milliseconds.", required = true, min = "100")
	int frequency() default 100;

}

@RequireConfigurerExtender
@RequireColor3LedDevice(versionStr = "1.0.0")
@Component(name = "com.daghan.heatmap", configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = DiscoballConfig.class)
public class Raspberry_piApplication {
	public enum StartStopEnum {
		START, STOP;
	}

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
			rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
					DeviceExecutorMethodTypeEnum.POST);
			rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
					DeviceExecutorMethodTypeEnum.POST);
			rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
					DeviceExecutorMethodTypeEnum.POST);
			break;
		}
	}

	@Deactivate
	public void deactivate()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		runThread = false;
		rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
				DeviceExecutorMethodTypeEnum.POST);
		rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
				DeviceExecutorMethodTypeEnum.POST);
		rm.activateResource(_threeColorDevice.getId(), ColorDtoFactory.NO_COLOR, Void.TYPE,
				DeviceExecutorMethodTypeEnum.POST);
		s_logger.info("stopping now");
	}

	private class DiscoThread extends Thread {
		@Override
		public void run() {
			int colorScheme = 0;
			while (runThread) {
				// disco ball!!!!

				try {
					updateColor(colorScheme);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | InterruptedException e) {
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
			Thread.sleep(config.frequency());
		}
	}
}
