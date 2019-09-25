package com.yuuyoo.ocr.controller;

import com.yuuyoo.ocr.entity.OCRResult;
import com.yuuyoo.ocr.enums.BaiduAIType;
import com.yuuyoo.ocr.enums.Platform;
import com.yuuyoo.ocr.respository.OCRResultRepository;
import com.yuuyoo.ocr.service.OcrService;
import java.io.IOException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class PhotoOcrController {

  @Autowired
  private OcrService ocrService;

  @Autowired
  private OCRResultRepository ocrResultRepository;

  /**
   * 上传图片并ocr .
   *
   * @param multipartFile 图片 .
   * @param platform 支付平台 Wechat/Alipay.
   */
  @PostMapping(value = "/v1/ocr/file")
  public ResponseEntity<?> ocr(@RequestPart("file") MultipartFile multipartFile,
      @RequestParam("platform") Platform platform) {
    try {
      String base64Str = Base64.getEncoder().encodeToString(multipartFile.getBytes());
      return ResponseEntity.ok(ocrService.ocr(base64Str, BaiduAIType.Receipt, platform));
    } catch (IOException e) {
      log.error("get bytes from multipartFile error \n", e);
    }
    return ResponseEntity.badRequest().build();
  }

  /**
   * 通过图片base64 ocr .
   *
   * @param base64String base64 .
   * @param platform 支付平台 Wechat/Alipay.
   */
  @PostMapping("/v1/ocr/base64String")
  public ResponseEntity<?> ocr(@RequestBody String base64String,
      @RequestParam("platform") Platform platform) {

    return ResponseEntity.ok(ocrService.ocr(base64String, BaiduAIType.Receipt, platform));
  }

  /**
   * 通过自定义模版识别图片 .
   *
   * @param multipartFile 图片 .
   * @param platform 支付平台 Wechat/Alipay.
   */
  @PostMapping(value = "/v1/ocr/file/template")
  public ResponseEntity<?> ocrWithTemplate(@RequestPart("file") MultipartFile multipartFile,
      @RequestParam("platform") Platform platform) {
    try {
      String base64Str = Base64.getEncoder().encodeToString(multipartFile.getBytes());
      int imageHash = base64Str.hashCode();
      OCRResult ocrResult = ocrResultRepository.findByImageHash(imageHash);
      if (ocrResult != null) {
        return ResponseEntity.ok(ocrResult);
      }
      return ResponseEntity.ok(ocrService.ocr(base64Str, BaiduAIType.TemplateRecognise, platform));
    } catch (IOException e) {
      log.error("get bytes from multipartFile error \n", e);
    }
    return ResponseEntity.badRequest().build();
  }
}
