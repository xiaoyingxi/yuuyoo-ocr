package com.yuuyoo.ocr.exception;

public class TokenException extends RuntimeException {

  private TokenException() {
  }

  public TokenException(String message) {
    super(message);
  }
}
