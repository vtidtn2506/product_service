package com.example.khoilnm.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private String id;

    private String title;

    private String categoryId;

    private int price;
}
