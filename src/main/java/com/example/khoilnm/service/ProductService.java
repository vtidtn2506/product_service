package com.example.khoilnm.service;

import com.example.khoilnm.dto.request.DeductStockRequest;
import com.example.khoilnm.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts();

    List<ProductResponse> getProductsByIds(List<String> ids);

    ProductResponse getProduct(String id);

    List<ProductResponse> deductStock(DeductStockRequest request);
}
