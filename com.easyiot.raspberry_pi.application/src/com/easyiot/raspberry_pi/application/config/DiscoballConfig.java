package com.easyiot.raspberry_pi.application.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.easyiot.raspberry_pi.application.StartStopEnum;

@ObjectClassDefinition(name = "Disco ball configuration")
public @interface DiscoballConfig {

	@AttributeDefinition(name = "Stop/Start", description = "Stops or start the disco ball.")
	StartStopEnum start() default StartStopEnum.START;

	@AttributeDefinition(name = "Frequency", description = "Frequency in milliseconds.", required = true, min = "100")
	int frequency() default 100;

}
