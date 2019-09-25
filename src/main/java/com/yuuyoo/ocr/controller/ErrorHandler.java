package com.yuuyoo.ocr.controller;

import com.yuuyoo.ocr.exception.OcrException;
import com.yuuyoo.ocr.exception.TokenException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @Builder
  @Value
  static class CustomError {

    String message;
    String code;
  }

  /**
   * 参数异常 .
   * @param ex .
   * @return
   */
  @ExceptionHandler({IllegalArgumentException.class})
  public @ResponseBody
  ResponseEntity<CustomError> handleIllegalOrImageException(Exception ex) {
    logger.error(ex.getClass().getSimpleName(), ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        CustomError.builder().message(ex.getMessage()).code(
            String.valueOf(HttpStatus.BAD_REQUEST.value())).build());
  }

  /**
   * token | OCR 获取异常 .
   * @param ex .
   * @return
   */
  @ExceptionHandler({TokenException.class, OcrException.class})
  public @ResponseBody
  ResponseEntity<CustomError> handleTokenOrOCRException(Exception ex) {
    logger.error(ex.getClass().getSimpleName(), ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        CustomError.builder().message(ex.getMessage()).code(
            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).build());
  }

  /**
   * 通用异常 .
   * @param ex .
   * @param request .
   * @return
   */
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<CustomError> globalErrorHandler(Exception ex, WebRequest request)
      throws Exception {
    logger.error("unexpected exception:", ex);

    ResponseEntity<Object> objectResponseEntity = this.handleException(ex, request);
    if (objectResponseEntity == null) {
      throw new Exception("server error");
    }
    HttpStatus status = objectResponseEntity.getStatusCode();

    return ResponseEntity.status(status).body(CustomError.builder()
        .message("ocr server error").code(String.valueOf(status.value())).build());
  }
}
