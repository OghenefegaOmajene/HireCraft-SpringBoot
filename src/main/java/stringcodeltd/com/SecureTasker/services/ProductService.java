package stringcodeltd.com.SecureTasker.services;

import stringcodeltd.com.SecureTasker.dtos.requests.ProductRequest;
import stringcodeltd.com.SecureTasker.dtos.response.MessageResponse;

import java.util.List;

public interface ProductService {
   ProductRequest createProduct(ProductRequest dto, Long userId);
   List<ProductRequest> getAllProducts();
   ProductRequest getProductById(Long id);
   List<ProductRequest> getProductsByUser(Long userId);
   MessageResponse deleteProduct(Long productId, Long userId);
   List<ProductRequest> searchProducts(String keyword);
}
