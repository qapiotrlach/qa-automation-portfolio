package io.github.qapiotrlach.playground.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Product API - write operations")
class ProductWriteApiTest extends ApiTestBase {

    /** Ids created by the test, cleaned up once it finishes. */
    private final List<Integer> createdIds = new ArrayList<>();

    @AfterEach
    void removeCreatedProducts() {
        createdIds.forEach(id ->
                given().spec(requestSpec).when().delete("/api/products/{id}", id));
        createdIds.clear();
    }

    private int createProduct(Map<String, Object> body) {
        int id = given().spec(requestSpec)
                .body(body)
                .when().post("/api/products")
                .then().statusCode(201)
                .extract().path("id");
        createdIds.add(id);
        return id;
    }

    @Test
    @DisplayName("creating a product returns 201, a Location header and the stored data")
    void shouldCreateProduct() {
        Map<String, Object> request = ProductFixtures.validProduct("QA test keyboard");

        Response response = given().spec(requestSpec)
                .body(request)
                .when().post("/api/products")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("name", equalTo("QA test keyboard"))
                .body("category", equalTo("testy"))
                .body("stock", equalTo(10))
                .extract().response();

        int id = response.path("id");
        createdIds.add(id);

        assertThat(response.header("Location"))
                .as("Location header must point at the created resource")
                .endsWith("/api/products/" + id);

        // The resource must actually be reachable under the advertised address.
        given().spec(requestSpec)
                .when().get("/api/products/{id}", id)
                .then().spec(jsonOkSpec)
                .body("name", equalTo("QA test keyboard"));
    }

    @Test
    @DisplayName("updating a product changes its data and the updatedAt timestamp")
    void shouldUpdateProduct() {
        int id = createProduct(ProductFixtures.validProduct("Product before update"));

        String beforeUpdate = given().spec(requestSpec)
                .when().get("/api/products/{id}", id)
                .then().spec(jsonOkSpec)
                .extract().path("updatedAt");

        Map<String, Object> changed = ProductFixtures.validProduct("Product after update");
        changed.put("price", 249.50);
        changed.put("stock", 3);

        given().spec(requestSpec)
                .body(changed)
                .when().put("/api/products/{id}", id)
                .then().spec(jsonOkSpec)
                .body("id", equalTo(id))
                .body("name", equalTo("Product after update"))
                .body("price", equalTo(249.50f))
                .body("stock", equalTo(3));

        String afterUpdate = given().spec(requestSpec)
                .when().get("/api/products/{id}", id)
                .then().spec(jsonOkSpec)
                .extract().path("updatedAt");

        assertThat(afterUpdate)
                .as("updatedAt must change after an edit")
                .isNotEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("deleting a product returns 204 and makes it unreachable")
    void shouldDeleteProduct() {
        int id = createProduct(ProductFixtures.validProduct());

        given().spec(requestSpec)
                .when().delete("/api/products/{id}", id)
                .then().statusCode(204);

        given().spec(requestSpec)
                .when().get("/api/products/{id}", id)
                .then().statusCode(404);

        createdIds.remove(Integer.valueOf(id));
    }

    @Test
    @DisplayName("deleting an unknown product returns 404")
    void shouldReturn404WhenDeletingUnknownProduct() {
        given().spec(requestSpec)
                .when().delete("/api/products/{id}", 999_999)
                .then().statusCode(404);
    }

    static Stream<Arguments> invalidPayloads() {
        return Stream.of(
                Arguments.of("blank name", ProductFixtures.withField("name", ""), "name"),
                Arguments.of("missing name", ProductFixtures.withoutField("name"), "name"),
                Arguments.of("negative price", ProductFixtures.withField("price", -1), "price"),
                Arguments.of("missing price", ProductFixtures.withoutField("price"), "price"),
                Arguments.of("negative stock", ProductFixtures.withField("stock", -5), "stock"),
                Arguments.of("blank category", ProductFixtures.withField("category", "  "), "category")
        );
    }

    @ParameterizedTest(name = "{0} is rejected with a validation error on {2}")
    @MethodSource("invalidPayloads")
    void shouldRejectInvalidPayload(String description, Map<String, Object> body, String expectedField) {
        given().spec(requestSpec)
                .body(body)
                .when().post("/api/products")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("type", equalTo("https://qa-playground/errors/validation"))
                .body("violations.field", hasItem(expectedField));
    }

    @Test
    @DisplayName("a rejected request does not create any resource")
    void shouldNotCreateResourceForRejectedRequest() {
        int before = given().spec(requestSpec)
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .extract().path("totalElements");

        given().spec(requestSpec)
                .body(ProductFixtures.withField("name", ""))
                .when().post("/api/products")
                .then().statusCode(400);

        int after = given().spec(requestSpec)
                .when().get("/api/products")
                .then().spec(jsonOkSpec)
                .extract().path("totalElements");

        assertThat(after)
                .as("a rejected request must not increase the product count")
                .isEqualTo(before);
    }
}
