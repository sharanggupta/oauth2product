package dev.sharanggupta.oauth2product;

import dev.sharanggupta.oauth2product.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestContainersConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class Oauth2productApplicationTests {

	@Test
	void contextLoads() {
	}
}