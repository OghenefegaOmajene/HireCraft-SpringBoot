package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.ProductRequest;
import HireCraft.com.SpringBoot.models.Product;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.repository.ProductRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.ProductService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProductRequest createProduct(ProductRequest dto, Long userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setContactInfo(dto.getContactInfo() != null ? dto.getContactInfo() : seller.getPhoneNumber());
        product.setSeller(seller);
        product.setPostedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Override
    public List<ProductRequest> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductRequest getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        return mapToDTO(product);
    }

    @Override
    public List<ProductRequest> getProductsByUser(Long userId) {
        return productRepository.findBySellerId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        if (!product.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this product");
        }

        productRepository.delete(product);

        return "Product deleted Successfully";
    }

    @Override
    public List<ProductRequest> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mapper
    private ProductRequest mapToDTO(Product product) {
        ProductRequest dto = new ProductRequest();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setContactInfo(product.getContactInfo());
        dto.setPostedAt(product.getPostedAt());
        dto.setSellerUsername(product.getSellerUsername());
        return dto;
    }
}

