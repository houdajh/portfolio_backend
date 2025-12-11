package com.devBrain.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DevBrainBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevBrainBackendApplication.class, args);
	}

}
