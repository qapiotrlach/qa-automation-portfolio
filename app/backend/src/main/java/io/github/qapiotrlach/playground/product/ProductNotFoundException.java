package io.github.qapiotrlach.playground.product;

public class ProductNotFoundException extends RuntimeException {

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Nie znaleziono produktu o identyfikatorze " + productId);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
