package dev.sharanggupta.oauth2product;

import org.springframework.boot.SpringApplication;

public class TestOauth2productApplication {

	public static void main(String[] args) {
		SpringApplication.from(Oauth2productApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
