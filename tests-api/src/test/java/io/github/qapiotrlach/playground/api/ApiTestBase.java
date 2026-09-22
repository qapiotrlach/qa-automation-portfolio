package io.github.qapiotrlach.playground.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * Shared configuration for API tests.
 *
 * Request and response specifications live in one place so that each test
 * describes only what it actually verifies.
 */
public abstract class ApiTestBase {

    protected static final String BASE_URL =
            System.getProperty("api.base.url", "http://localhost:8080");

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification jsonOkSpec;

    @BeforeAll
    static void configureRestAssured() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        jsonOkSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();

        // On failure, dump the full request and response - otherwise diagnosing CI runs is guesswork.
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }
}
