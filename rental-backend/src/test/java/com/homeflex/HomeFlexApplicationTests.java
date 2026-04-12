package com.homeflex;

import com.homeflex.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration smoke test — requires PostgreSQL, Redis, RabbitMQ and Elasticsearch.
 * Uses Testcontainers via BaseIntegrationTest.
 */
@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class HomeFlexApplicationTests extends BaseIntegrationTest {

	@Test
	void contextLoads() {
	}

}
