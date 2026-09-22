package io.github.qapiotrlach.playground.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Product API - read operations")
class ProductReadApiTest extends ApiTestBase {

    @Test
    @DisplayName("product list returns 200 with pagination metadata")
    void shouldReturnPaginationMetadata() {
        given().spec(requestSpec)
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .body("content", notNullValue())
                .body("totalElements", greaterThan(0))
                .body("totalPages", greaterThan(0))
                .body("number", equalTo(0));
    }

    @ParameterizedTest(name = "size={0} limits the number of items on a page")
    @ValueSource(ints = {1, 2, 5})
    void shouldLimitPageSize(int size) {
        List<Object> content = given().spec(requestSpec)
                .queryParam("size", size)
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .body("size", equalTo(size))
                .extract().jsonPath().getList("content");

        assertThat(content)
                .as("page must not contain more items than the requested size")
                .hasSizeLessThanOrEqualTo(size);
    }

    @Test
    @DisplayName("category filter returns only products from that category")
    void shouldFilterByCategory() {
        List<String> categories = given().spec(requestSpec)
                .queryParam("category", "audio")
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .extract().jsonPath().getList("content.category");

        assertThat(categories)
                .as("filtering by the audio category")
                .isNotEmpty()
                .allMatch(category -> category.equalsIgnoreCase("audio"));
    }

    @Test
    @DisplayName("name search is case insensitive")
    void shouldSearchByNameIgnoringCase() {
        List<String> names = given().spec(requestSpec)
                .queryParam("name", "monitor")
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .extract().jsonPath().getList("content.name");

        assertThat(names)
                .isNotEmpty()
                .allMatch(name -> name.toLowerCase().contains("monitor"));
    }

    @Test
    @DisplayName("fetching an existing product returns all fields")
    void shouldReturnCompleteProduct() {
        Integer firstId = given().spec(requestSpec)
                .queryParam("size", 1)
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .extract().jsonPath().get("content[0].id");

        Response response = given().spec(requestSpec)
                .pathParam("id", firstId)
                .when().get("/api/products/{id}")
                .then().spec(jsonOkSpec)
                .extract().response();

        assertThat(response.jsonPath().getInt("id")).isEqualTo(firstId);
        assertThat(response.jsonPath().getString("name")).isNotBlank();
        assertThat(response.jsonPath().getString("category")).isNotBlank();
        assertThat(response.jsonPath().getDouble("price")).isGreaterThanOrEqualTo(0.0);
        assertThat(response.jsonPath().getString("createdAt")).isNotBlank();
    }

    @Test
    @DisplayName("unknown product returns 404 as problem+json")
    void shouldReturnProblemDetailForUnknownProduct() {
        given().spec(requestSpec)
                .when().get("/api/products/{id}", 999_999)
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("status", equalTo(404))
                .body("type", equalTo("https://qa-playground/errors/product-not-found"))
                .body("productId", equalTo(999_999))
                .body("detail", notNullValue());
    }
}
