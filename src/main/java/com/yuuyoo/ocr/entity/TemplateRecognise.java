package com.yuuyoo.ocr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TemplateRecognise {

  @JsonProperty(value = "error_code", required = false)
  private Integer errorCode;
  @JsonProperty(value = "error_msg", required = false)
  private String errorMsg;
  private OCRResult data;
}
