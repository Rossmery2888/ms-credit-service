package com.example.mscreditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsCreditServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCreditServiceApplication.class, args);
	}

}
