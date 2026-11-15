package com.example.khoilnm.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeductStockItemRequest {
    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;
}
