package com.yuuyoo.ocr.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuuyoo.ocr.client.BaiduAIClient.OCRResult.Location;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orc_result")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public class OCRResult {

  private Integer imageHash;
  private List<OCRRow> ret;
  private String templateSign;
  private String templateName;
  private String logId;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class OCRRow {

    private Location location;
    @JsonProperty(value = "word_name", required = false)
    private String wordName;
    private String word;
  }

}
