package com.yuuyoo.ocr.controller;

import com.yuuyoo.ocr.client.BaiduAIClient.AccessToken;
import com.yuuyoo.ocr.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocr/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @GetMapping
  public String getAuth() {
    return authService.getAuth();
  }

  @GetMapping("/access_token")
  public AccessToken getAccessToken() {
    return authService.getAccessToken();
  }

}
