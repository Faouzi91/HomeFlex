package com.homeflex;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration smoke test — requires PostgreSQL, Redis, RabbitMQ and Elasticsearch.
 * Excluded from unit test runs. Run with: {@code ./gradlew test -PincludeTags=integration}
 */
@SpringBootTest(classes = HomeFlexApplication.class)
@ActiveProfiles("test")
@Tag("integration")
class HomeFlexApplicationTests {

	@Test
	void contextLoads() {
	}

}
