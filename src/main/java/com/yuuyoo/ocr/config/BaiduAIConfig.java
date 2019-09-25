package com.yuuyoo.ocr.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "baidu.ai.ocr")
@Data
public class BaiduAIConfig {

  @Data
  public static class Token {

    String id;
    String name;
    String apiKey;
    String secretKey;
  }

  @Data
  public static class Template {

    String platform;
    String id;
  }

  List<Template> templates = new ArrayList<>();
  List<Token> tokens = new ArrayList<>();
}
