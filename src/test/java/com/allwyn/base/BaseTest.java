package com.allwyn.base;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Base test class providing shared test setup, logging, and assertion utilities.
 * By default all test classes should extend this class so they can inherit its methods.
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @BeforeClass(alwaysRun = true)
    @Parameters("base.url")
    public void baseSetUp(String baseUrl) {

        System.setProperty("base.url", baseUrl);

        log.info("========================================");
        log.info("Starting test suite: {}", getClass().getSimpleName());
        log.info("========================================");
    }

    // ==================== Common Assertion Helpers ====================

    /**
     * Asserts that the response has the expected HTTP status code.
     */
    protected void assertStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode())
                .as("Expected HTTP status code %d but got %d. Response body: %s",
                        expectedStatusCode, response.getStatusCode(), response.getBody().asString())
                .isEqualTo(expectedStatusCode);
    }

    /**
     * Asserts that the response content type is JSON.
     */
    protected void assertJsonContentType(Response response) {
        assertThat(response.getContentType())
                .as("Expected JSON content type")
                .containsIgnoringCase("application/json");
    }

    /**
     * Asserts that the response time is within acceptable limits.
     */
    protected void assertResponseTimeWithinLimit(Response response, long maxMillis) {
        assertThat(response.getTime())
                .as("Response time should be within %d ms", maxMillis)
                .isLessThan(maxMillis);
    }

    /**
     * Creates a new SoftAssert instance for tests needing multiple assertions.
     */
    protected SoftAssert createSoftAssert() {
        return new SoftAssert();
    }
}
