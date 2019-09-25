package com.yuuyoo.ocr.enums;

public enum Platform {

  Wechat("微信"),
  Alipay("支付宝");

  private String desc;

  Platform(String desc) {
    this.desc = desc;
  }
}
