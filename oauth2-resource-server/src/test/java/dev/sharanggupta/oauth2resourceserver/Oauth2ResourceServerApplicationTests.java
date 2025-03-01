package dev.sharanggupta.oauth2resourceserver;

import dev.sharanggupta.oauth2resourceserver.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestContainersConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class Oauth2ResourceServerApplicationTests extends BaseIntegrationTest{

	@Test
	void contextLoads() {
		// This test is empty because it is only used to check if the Spring context loads successfully
	}
}