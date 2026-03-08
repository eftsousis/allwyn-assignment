package com.allwyn.clients;

import com.allwyn.config.ApiConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.concurrent.TimeUnit;

/**
 * Base API client providing shared configuration and utility methods.
 * All endpoint-specific clients extend this class.
 */
public abstract class BaseApiClient {

    private final RequestSpecification requestSpec;

    // contructor for BaseApiClient
    protected BaseApiClient() {
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                // logs req/responses to Allure report
                .addFilter(new AllureRestAssured())
                // comment out to print less logs on std output
                .log(LogDetail.ALL)
                .build();

        // configuration parameters
        RestAssured.config = RestAssured.config()
                .httpClient(RestAssured.config().getHttpClientConfig()
                        .setParam("http.connection.timeout",
                                (int) TimeUnit.SECONDS.toMillis(ApiConfig.CONNECTION_TIMEOUT))
                        .setParam("http.socket.timeout",
                                (int) TimeUnit.SECONDS.toMillis(ApiConfig.RESPONSE_TIMEOUT)));
    }

    /**
     * Returns the base request specification for making API calls.
     */
    protected RequestSpecification givenRequest() {
        return RestAssured.given()
                .spec(requestSpec);
    }

    /**
     * Returns a public request specification for custom test scenarios
     * that need direct access (e.g., malformed body tests).
     */
    public RequestSpecification givenBaseRequest() {
        return givenRequest();
    }

    /**
     * Performs a GET request to the specified path.
     */
    protected Response get(String path) {
        return givenRequest()
                .when()
                .get(path);
    }

    /**
     * Performs a GET request with a path parameter.
     */
    protected Response getById(String path, int id) {
        return givenRequest()
                .pathParam("id", id)
                .when()
                .get(path);
    }

    /**
     * Performs a POST request with the given body.
     */
    protected Response post(String path, Object body) {
        return givenRequest()
                .body(body)
                .when()
                .post(path);
    }

    /**
     * Performs a PUT request with the given body and path parameter.
     */
    protected Response put(String path, int id, Object body) {
        return givenRequest()
                .pathParam("id", id)
                .body(body)
                .when()
                .put(path);
    }

    /**
     * Performs a DELETE request with a path parameter.
     */
    protected Response delete(String path, int id) {
        return givenRequest()
                .pathParam("id", id)
                .when()
                .delete(path);
    }
}
