package com.example.khoilnm.exception;

import com.khoilnm.commoncore.response.BaseResponse;
import com.khoilnm.commoncore.response.BaseResponseCode;
import com.khoilnm.commoncore.response.ResponseCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * Request-level errors that common-core's GlobalExceptionHandler would otherwise turn into 500.
 * Must run before GlobalExceptionHandler (which catches Exception), hence HIGHEST_PRECEDENCE.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestExceptionHandler {
    private static final ResponseCode BAD_REQUEST = new BaseResponseCode("SYSTEM400", HttpStatus.BAD_REQUEST);
    private static final ResponseCode NOT_FOUND = new BaseResponseCode("SYSTEM404", HttpStatus.NOT_FOUND);
    private static final ResponseCode BUSY = new BaseResponseCode("SYSTEM503", HttpStatus.SERVICE_UNAVAILABLE);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return BaseResponse.error(BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse<Object> handleUnreadable(HttpMessageNotReadableException ex) {
        return BaseResponse.error(BAD_REQUEST, "Malformed request body");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public BaseResponse<Object> handleNoResource(NoResourceFoundException ex) {
        return BaseResponse.error(NOT_FOUND, "Resource not found: /" + ex.getResourcePath());
    }

    /** No DB connection available within hikari.connection-timeout: nothing was executed. */
    @ExceptionHandler(CannotCreateTransactionException.class)
    public BaseResponse<Object> handleNoConnection(CannotCreateTransactionException ex) {
        return BaseResponse.error(BUSY, "Service is busy, please retry");
    }
}
