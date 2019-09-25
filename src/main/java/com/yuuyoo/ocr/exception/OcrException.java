package com.yuuyoo.ocr.exception;

public class OcrException extends RuntimeException {

  private OcrException() {
  }

  public OcrException(String message) {
    super(message);
  }
}
