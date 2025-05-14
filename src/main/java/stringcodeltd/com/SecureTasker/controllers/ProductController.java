package stringcodeltd.com.SecureTasker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stringcodeltd.com.SecureTasker.dtos.requests.ProductRequest;
import stringcodeltd.com.SecureTasker.services.ProductService;
import stringcodeltd.com.SecureTasker.services.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest dto, Principal principal) {
        Long userId = userService.getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(productService.createProduct(dto, userId));
    }

    @GetMapping
    public List<ProductRequest> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductRequest getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}

