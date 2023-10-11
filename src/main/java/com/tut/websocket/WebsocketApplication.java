package com.tut.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@EnableScheduling
@SpringBootApplication
public class WebsocketApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketApplication.class);

	public static void main(String[] args) throws UnknownHostException {
		ConfigurableApplicationContext application= SpringApplication.run(WebsocketApplication.class, args);
		Environment env = application.getEnvironment();
		LOGGER.info("\n[----------------------------------------------------------]\n\t" +
						"チャットルームの起動に成功しました！クリックしてアクセス：\thttp://{}:{}" +
					"\n[----------------------------------------------------------",
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}
}
