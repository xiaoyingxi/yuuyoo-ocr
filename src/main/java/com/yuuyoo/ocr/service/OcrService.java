package com.yuuyoo.ocr.service;

import com.google.common.collect.Maps;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.yuuyoo.ocr.client.BaiduAIClient;
import com.yuuyoo.ocr.client.BaiduAIClient.AccessToken;
import com.yuuyoo.ocr.config.BaiduAIConfig;
import com.yuuyoo.ocr.config.BaiduAIConfig.Template;
import com.yuuyoo.ocr.entity.TemplateRecognise;
import com.yuuyoo.ocr.enums.BaiduAIType;
import com.yuuyoo.ocr.enums.Platform;
import com.yuuyoo.ocr.exception.OcrException;
import com.yuuyoo.ocr.respository.OCRResultRepository;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OcrService {

  @Autowired
  private BaiduAIClient baiduAIClient;

  @Autowired
  private AuthService authService;

  @Autowired
  private BaiduAIConfig baiduAIConfig;

  @Autowired
  private OCRResultRepository ocrResultRepository;

  /**
   * baidu ai ocr .
   * @param base64 base64 string .
   * @param ocrType  ocr type:  AccurateBasic, Receipt, TemplateRecognise.
   * @param platform platform: Wechat | Alipay .
   * @return
   */
  public Object ocr(String base64, BaiduAIType ocrType, Platform platform) {
    final Map<String, Object> parameters = Maps.newHashMapWithExpectedSize(2);
    parameters.put("image", base64);
    switch (ocrType) {
      case Receipt:
      case AccurateBasic:
        break;
      case TemplateRecognise:
        List<Template> templates = baiduAIConfig.getTemplates();
        if (CollectionUtils.isEmpty(templates)) {
          throw new OcrException("can not find any template!");
        }
        Template template = templates.stream()
            .filter(tt -> StringUtils.equals(tt.getPlatform(), platform.name()))
            .findFirst()
            .orElseThrow(() -> new OcrException("no matching template!"));
        parameters.put("templateSign", template.getId());
        break;
      default:
        break;
    }
    return execute(parameters, ocrType, base64.hashCode());
  }

  private Object execute(Map<String, Object> param,  BaiduAIType ocrType, Integer imageHash) {
    AccessToken accessToken = authService.getAccessToken();
    ResponseEntity<?> result = null;
    switch (ocrType) {
      case Receipt:
        result = baiduAIClient.receiptOCR(accessToken.getAccessToken(), param);
        break;
      case AccurateBasic:
        result = baiduAIClient.accurateBasicOCR(accessToken.getAccessToken(), param);
        break;
      case TemplateRecognise:
        result = baiduAIClient.recogniseOCR(accessToken.getAccessToken(), param);
        break;
      default:
        break;
    }
    if (result != null && result.getStatusCode().is2xxSuccessful()) {
      TemplateRecognise templateRecognise = (TemplateRecognise) result.getBody();
      if (templateRecognise == null) {
        return null;
      }
      ocrResultRepository
          .insert(templateRecognise.getData().toBuilder().imageHash(imageHash).build());
      return templateRecognise;
    }
    throw new OcrException("ocr fail!");
  }
}
