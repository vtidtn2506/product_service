package com.example.khoilnm.controller;

import com.example.khoilnm.dto.response.ProductResponse;
import com.example.khoilnm.service.ProductService;
import com.khoilnm.commoncore.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public BaseResponse<List<ProductResponse>> getProducts(@RequestParam(required = false) List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            return BaseResponse.success(productService.getProductsByIds(ids));
        }
        return BaseResponse.success(productService.getProducts());
    }

    @GetMapping("/{id}")
    public BaseResponse<ProductResponse> getProduct(@PathVariable String id) {
        return BaseResponse.success(productService.getProduct(id));
    }
}
