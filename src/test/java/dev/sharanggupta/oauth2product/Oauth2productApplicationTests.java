package dev.sharanggupta.oauth2product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
class Oauth2productApplicationTests {

	@Test
	void contextLoads() {
	}

}
