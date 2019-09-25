package com.yuuyoo.ocr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuuyoo.ocr.client.BaiduAIClient;
import com.yuuyoo.ocr.client.BaiduAIClient.AccessToken;
import com.yuuyoo.ocr.config.BaiduAIConfig;
import com.yuuyoo.ocr.config.BaiduAIConfig.Token;
import com.yuuyoo.ocr.exception.TokenException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * 获取token类 .
 */

@Service
@Slf4j
public class AuthService {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BaiduAIConfig baiduAIConfig;

  @Autowired
  private BaiduAIClient baiduAIClient;

  @Value("${baidu.ai.ocr.authHost:https://aip.baidubce.com/oauth/2.0/token}")
  private String authHost;

  /**
   * 获取权限token .
   *
   * @return 返回示例：
   *      { "access_token":
   *          "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567" .
   *       "expires_in": 2592000 } .
   */
  public String getAuth() {
    // 官网获取的 API Key 更新为你注册的
    // 官网获取的 Secret Key 更新为你注册的
    List<Token> tokens = baiduAIConfig.getTokens();
    if (CollectionUtils.isNotEmpty(tokens)) {
      Token token = tokens.get(0);
      return getAuth(token.getApiKey(), token.getSecretKey());
    }
    log.error("没有找到百度AI token配置");
    return null;
  }

  /**
   * 获取API访问token 该token有一定的有效期，需要自行管理，当失效时需重新获取 .
   *
   * @param ak - 百度云官网获取的 API Key .
   * @param sk - 百度云官网获取的 Securet Key .
   * @return assess_token 示例：
   *         "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567".
   */
  public String getAuth(String ak, String sk) {
    // 获取token地址

    String getAccessTokenUrl = authHost
        + "?"
        // 1. grant_type为固定参数
        + "grant_type=client_credentials"
        // 2. 官网获取的 API Key
        + "&client_id=" + ak
        // 3. 官网获取的 Secret Key
        + "&client_secret=" + sk;
    try {
      URL realUrl = new URL(getAccessTokenUrl);
      // 打开和URL之间的连接
      HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      // 遍历所有的响应头字段
      map.entrySet().stream()
          .forEach(mm -> log.info(mm.getKey() + "--->" + mm.getValue()));
      // 定义 BufferedReader输入流来读取URL的响应
      StringBuilder result = new StringBuilder();
      try (BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
        String line;
        while ((line = in.readLine()) != null) {
          result.append(line);
        }
      }
      /**
       * 返回结果示例
       */
      log.info("result:{}", result.toString());
      Map resultMap = objectMapper.readValue(result.toString(), Map.class);
      String accessToken = MapUtils.getString(resultMap, "access_token");
      return accessToken;
    } catch (Exception e) {
      log.error("获取token失败！", e);
    }
    return null;
  }

  /**
   * get access token .
   * @return
   */
  public AccessToken getAccessToken() {
    List<Token> tokens = baiduAIConfig.getTokens();
    if (CollectionUtils.isNotEmpty(tokens)) {
      Token aiApp = tokens.get(0);
      ResponseEntity<AccessToken> accessTokenResponse = baiduAIClient
          .token("client_credentials", aiApp.getApiKey(), aiApp.getSecretKey());
      if (accessTokenResponse != null && accessTokenResponse.getStatusCode().is2xxSuccessful()) {
        final BaiduAIClient.AccessToken accessToken = accessTokenResponse.getBody();
        log.debug("Got access token '{}'.", accessToken);
        if (accessToken != null && StringUtils.isNotBlank(accessToken.getAccessToken())) {
          return accessToken;
        }
        final String errorMsg = MessageFormatter.format(
            "Failed to obtain access token of Appid '{}' with response '{}'.",
            aiApp.getId(), accessToken).getMessage();
        log.error(errorMsg);
        throw new TokenException(errorMsg);
      }
    } else {
      throw new TokenException("no one token is configured");
    }
    return null;
  }
}
