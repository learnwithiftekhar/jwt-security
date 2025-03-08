package com.learnwithiftekhar.jwtsecurity.controller;

import com.learnwithiftekhar.jwtsecurity.dto.ApiResponse;
import com.learnwithiftekhar.jwtsecurity.model.Product;
import com.learnwithiftekhar.jwtsecurity.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> productList = productService.getAllProducts();
        return ResponseEntity.ok(
                ApiResponse.success(productList, "product list retrieved successfully", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id).orElse(null);
        return ResponseEntity.ok(
                ApiResponse.success(product, "product retrieved successfully", HttpStatus.OK)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> addProduct(@RequestBody Product product) {
        Product newProduct = productService.saveProduct(product);
        return ResponseEntity.ok(
                ApiResponse.success(newProduct, "product added successfully", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id).orElse(null);
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "product deleted successfully", HttpStatus.OK)
        );
    }
}
