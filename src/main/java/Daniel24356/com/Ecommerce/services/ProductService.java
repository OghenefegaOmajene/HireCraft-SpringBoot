package Daniel24356.com.Ecommerce.services;

import Daniel24356.com.Ecommerce.dtos.requests.ProductRequest;
import Daniel24356.com.Ecommerce.dtos.response.MessageResponse;

import java.util.List;

public interface ProductService {
   ProductRequest createProduct(ProductRequest dto, Long userId);
   List<ProductRequest> getAllProducts();
   ProductRequest getProductById(Long id);
   List<ProductRequest> getProductsByUser(Long userId);
   String deleteProduct(Long productId, Long userId);
   List<ProductRequest> searchProducts(String keyword);
}
