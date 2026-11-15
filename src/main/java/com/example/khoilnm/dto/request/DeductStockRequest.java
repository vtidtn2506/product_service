package com.example.khoilnm.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeductStockRequest {
    @NotEmpty
    @Valid
    private List<DeductStockItemRequest> items;
}
