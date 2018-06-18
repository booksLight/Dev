package org.rk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.rk.web")
public class AmulApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmulApplication.class, args);
	}
}
