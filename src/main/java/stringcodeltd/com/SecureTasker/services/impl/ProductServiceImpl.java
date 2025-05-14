package stringcodeltd.com.SecureTasker.services.impl;

import stringcodeltd.com.SecureTasker.dtos.requests.ProductRequest;
import stringcodeltd.com.SecureTasker.dtos.response.MessageResponse;
import stringcodeltd.com.SecureTasker.services.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    @Override
    public ProductRequest createProduct(ProductRequest dto, Long userId) {
        return null;
    }

    @Override
    public List<ProductRequest> getAllProducts() {
        return List.of();
    }

    @Override
    public ProductRequest getProductById(Long id) {
        return null;
    }

    @Override
    public List<ProductRequest> getProductsByUser(Long userId) {
        return List.of();
    }

    @Override
    public MessageResponse deleteProduct(Long productId, Long userId) {
        return null;
    }

    @Override
    public List<ProductRequest> searchProducts(String keyword) {
        return List.of();
    }
}
