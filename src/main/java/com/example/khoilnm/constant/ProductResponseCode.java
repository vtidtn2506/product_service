package com.example.khoilnm.constant;

import com.khoilnm.commoncore.response.BaseResponseCode;
import com.khoilnm.commoncore.response.ResponseCode;
import org.springframework.http.HttpStatus;

public final class ProductResponseCode {
    public static final ResponseCode PRODUCT_NOT_FOUND = new BaseResponseCode("PRODUCT404", HttpStatus.NOT_FOUND);

    private ProductResponseCode() {
    }
}
