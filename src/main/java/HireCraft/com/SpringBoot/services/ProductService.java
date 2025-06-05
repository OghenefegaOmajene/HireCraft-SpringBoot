package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.ProductRequest;

import java.util.List;

public interface ProductService {
   ProductRequest createProduct(ProductRequest dto, Long userId);
   List<ProductRequest> getAllProducts();
   ProductRequest getProductById(Long id);
   List<ProductRequest> getProductsByUser(Long userId);
   String deleteProduct(Long productId, Long userId);
   List<ProductRequest> searchProducts(String keyword);
}
