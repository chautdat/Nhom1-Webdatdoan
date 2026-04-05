package com.nhom1ck.webdatdoan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class N1WebdatdoanApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(N1WebdatdoanApplication.class, args);
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        System.out.println("Server: http://localhost:" + port);
	}

}
