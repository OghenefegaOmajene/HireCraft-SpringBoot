package Daniel24356.com.Ecommerce.controllers;

import Daniel24356.com.Ecommerce.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Daniel24356.com.Ecommerce.dtos.requests.ProductRequest;
import Daniel24356.com.Ecommerce.services.ProductService;

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

