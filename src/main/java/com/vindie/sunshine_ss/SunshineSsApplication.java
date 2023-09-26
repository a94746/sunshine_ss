package com.vindie.sunshine_ss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SunshineSsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunshineSsApplication.class, args);
	}

}
