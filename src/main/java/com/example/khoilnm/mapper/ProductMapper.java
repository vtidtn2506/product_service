package com.example.khoilnm.mapper;

import com.example.khoilnm.dto.response.ProductResponse;
import com.example.khoilnm.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponses(List<Product> products);
}
