package dev.sharanggupta.oauth2product;

import dev.sharanggupta.oauth2product.config.TestContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestOauth2productApplication {

	public static void main(String[] args) {
		SpringApplication.from(Oauth2productApplication::main).with(TestContainersConfig.class).run(args);	}
}
