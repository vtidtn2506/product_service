package com.example.khoilnm.service.impl;

import com.example.khoilnm.constant.ProductResponseCode;
import com.example.khoilnm.dto.response.ProductResponse;
import com.example.khoilnm.entity.Product;
import com.example.khoilnm.mapper.ProductMapper;
import com.example.khoilnm.repository.ProductRepository;
import com.example.khoilnm.service.ProductService;
import com.khoilnm.commoncore.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private static final int NOT_DELETED = 0;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getProducts() {
        return productMapper.toResponses(productRepository.findAllByIsDelete(NOT_DELETED));
    }

    @Override
    public List<ProductResponse> getProductsByIds(List<String> ids) {
        return productMapper.toResponses(productRepository.findAllByIdInAndIsDelete(ids, NOT_DELETED));
    }

    @Override
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findByIdAndIsDelete(id, NOT_DELETED)
                .orElseThrow(() -> new BusinessException(ProductResponseCode.PRODUCT_NOT_FOUND, "Product not found: " + id,
                        ProductResponseCode.PRODUCT_NOT_FOUND.getDefaultHttpStatus()));
        return productMapper.toResponse(product);
    }
}
