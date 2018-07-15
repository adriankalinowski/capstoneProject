package edu.wayne.capstone.references;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@EnableEurekaClient
@SpringBootApplication
public class ReferencesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReferencesApplication.class, args);
	}
}
