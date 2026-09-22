package io.github.qapiotrlach.playground.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page<ProductResponse> findAll(String category, String name, Pageable pageable) {
        Page<Product> page;
        if (category != null && !category.isBlank()) {
            page = repository.findByCategoryIgnoreCase(category, pageable);
        } else if (name != null && !name.isBlank()) {
            page = repository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(ProductResponse::from);
    }

    public ProductResponse findById(Long id) {
        return repository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category(),
                request.active()
        );
        return ProductResponse.from(repository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category(),
                request.active()
        );
        return ProductResponse.from(repository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
