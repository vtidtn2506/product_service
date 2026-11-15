package com.example.khoilnm.constant;

import com.khoilnm.commoncore.response.BaseResponseCode;
import com.khoilnm.commoncore.response.ResponseCode;
import org.springframework.http.HttpStatus;

public final class ProductResponseCode {
    public static final ResponseCode PRODUCT_NOT_FOUND = new BaseResponseCode("PRODUCT404", HttpStatus.NOT_FOUND);
    public static final ResponseCode INSUFFICIENT_STOCK = new BaseResponseCode("PRODUCT_STOCK400", HttpStatus.BAD_REQUEST);
    /** Could not get the row lock in time (too many concurrent updates on the same product). */
    public static final ResponseCode STOCK_LOCK_TIMEOUT = new BaseResponseCode("PRODUCT_STOCK503", HttpStatus.SERVICE_UNAVAILABLE);

    private ProductResponseCode() {
    }
}
