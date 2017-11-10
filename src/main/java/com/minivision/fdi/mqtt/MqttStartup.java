package com.minivision.fdi.mqtt;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.minivision.fdi.mqtt.auth.FaceDeviceAuthenticator;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqttStartup {
	
	@Autowired
	private FaceDeviceAuthenticator camaraAuthenticator;
	
	@Autowired
	private Server mqttBroker;
	
	@Autowired
	private ServerInterceptHandler handler;
	
	@PostConstruct
	public void start() throws Exception {
		startBroker();
	}
	
	@PreDestroy
	public void stop() throws Exception {
	  mqttBroker.stopServer();
      log.info("MQTT Broker stopped");
	}

	private void startBroker() throws IOException {
		Properties props = new Properties();
		props.put(BrokerConstants.PORT_PROPERTY_NAME, Integer.toString(BrokerConstants.PORT));
		props.put(BrokerConstants.HOST_PROPERTY_NAME, BrokerConstants.HOST);
		props.put(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, BrokerConstants.DISABLED_PORT_BIND);
		props.put(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, Boolean.FALSE.toString());
		mqttBroker.startServer(new MemoryConfig(props),null,null,camaraAuthenticator,null);
		mqttBroker.addInterceptHandler(handler);
		// Bind a shutdown hook
		/*Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				mqttBroker.stopServer();
				logger.info("MQTT Broker stoped");
			}
		});*/
		log.info("MQTT Broker started");
	}
	
}
