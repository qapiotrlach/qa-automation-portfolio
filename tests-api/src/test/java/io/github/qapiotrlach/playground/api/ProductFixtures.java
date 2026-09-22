package io.github.qapiotrlach.playground.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds product test data. Every test creates its own data so that it never
 * depends on execution order or on the seeded catalogue.
 */
public final class ProductFixtures {

    private ProductFixtures() {
    }

    public static Map<String, Object> validProduct() {
        return validProduct("Test product " + System.nanoTime());
    }

    public static Map<String, Object> validProduct(String name) {
        Map<String, Object> product = new LinkedHashMap<>();
        product.put("name", name);
        product.put("description", "Created by an automated test");
        product.put("price", 199.99);
        product.put("stock", 10);
        product.put("category", "testy");
        product.put("active", true);
        return product;
    }

    public static Map<String, Object> withField(String field, Object value) {
        Map<String, Object> product = validProduct();
        product.put(field, value);
        return product;
    }

    public static Map<String, Object> withoutField(String field) {
        Map<String, Object> product = validProduct();
        product.remove(field);
        return product;
    }
}
