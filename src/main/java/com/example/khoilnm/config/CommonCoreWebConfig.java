package com.example.khoilnm.config;

import com.khoilnm.commoncore.exception.BaseResponseStatusAdvice;
import com.khoilnm.commoncore.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enables common-core's BaseResponse handling:
 * - GlobalExceptionHandler: BusinessException / Exception -> BaseResponse
 * - BaseResponseStatusAdvice: sets the HTTP status from BaseResponse.status
 */
@Configuration
@Import({GlobalExceptionHandler.class, BaseResponseStatusAdvice.class})
public class CommonCoreWebConfig {
}
