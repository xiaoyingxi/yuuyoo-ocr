package com.yuuyoo.ocr.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuuyoo.ocr.entity.TemplateRecognise;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.spring.SpringFormEncoder;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "baidu-ai", url = "https://aip.baidubce.com")
public interface BaiduAIClient {

  @Configuration
  class BaiduAIConfiguration {

    @Bean
    Logger.Level loggerLevel() {
      return Logger.Level.FULL;
    }

    @Bean
    @Primary
    @Scope("prototype")
    public Encoder feignFormEncoder() {
      return new SpringFormEncoder(new FormEncoder());
    }
  }

  @ToString
  public static class AccessToken {

    @JsonProperty(value = "refresh_token", required = false)
    @Getter
    String refreshToken;
    @Getter
    @JsonProperty(value = "expires_in", required = false)
    long expiresIn;
    @Getter
    @JsonProperty(value = "scope", required = false)
    String scope;
    @Getter
    @JsonProperty(value = "session_key", required = false)
    String sessionKey;
    @Getter
    @JsonProperty(value = "access_token", required = false)
    String accessToken;
    @Getter
    @JsonProperty(value = "session_secret", required = false)
    String sessionSecret;
    @Getter
    @JsonProperty(value = "error", required = false)
    String error;
    @Getter
    @JsonProperty(value = "error_description", required = false)
    String errorDescription;
    @Setter
    @Getter
    String ak;
  }

  @Getter
  public static class OCRResult {

    @Value
    public static class Location {

      @JsonProperty(value = "left", required = true)
      int left;
      @JsonProperty(value = "top", required = true)
      int top;
      @JsonProperty(value = "width", required = true)
      int width;
      @JsonProperty(value = "height", required = true)
      int height;
    }

    @JsonProperty(value = "direction", required = false)
    int direction = -1;
    @JsonProperty(value = "log_id", required = true)
    long logId;
    @JsonProperty(value = "words_result_num", required = false)
    int wordsNum;
    //@JsonProperty(value = "words_result", required = false)
    //List<Word> words;
    @JsonProperty(value = "error_code", required = false)
    int errorCode;
    @JsonProperty(value = "error_msg", required = false)
    String errorMsg;
  }

  /**
   * 获取 access token .
   *
   * @param type .
   * @param ak appKey .
   * @param sk secretKey .
   */
  @RequestMapping(method = RequestMethod.GET,
      value = "/oauth/2.0/token")
  ResponseEntity<AccessToken> token(@RequestParam("grant_type") String type,
      @RequestParam("client_id") String ak,
      @RequestParam("client_secret") String sk);

  /**
   * 通用票据识别 .
   *
   * @param token .
   * @param body .
   */
  @RequestMapping(method = RequestMethod.POST, value = "/rest/2.0/ocr/v1/receipt",
      headers = {"content-type=application/x-www-form-urlencoded"},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  ResponseEntity<Map<String, Object>> receiptOCR(@RequestParam("access_token") String token,
      Map<String, ?> body);

  /**
   * 高精度文字识别 .
   *
   * @param token .
   * @param body .
   */
  @RequestMapping(method = RequestMethod.POST, value = "rest/2.0/ocr/v1/accurate_basic",
      headers = {"content-type=application/x-www-form-urlencoded"})
  ResponseEntity<Map<String, Object>> accurateBasicOCR(@RequestParam("access_token") String token,
      Map<String, ?> body);

  /**
   * 自定义模版识别 .
   *
   * @param token .
   * @param body .
   */
  @RequestMapping(method = RequestMethod.POST, value = "/rest/2.0/solution/v1/iocr/recognise",
      headers = {"content-type=application/x-www-form-urlencoded"},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  ResponseEntity<TemplateRecognise> recogniseOCR(@RequestParam("access_token") String token,
      Map<String, ?> body);

}
