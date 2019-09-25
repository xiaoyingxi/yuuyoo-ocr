### Summary
---
- SpringBoot version `2.0.4.RELEASE`
- checkstylef
- spotbugs `1.6.1`

#### OCR
[Baidu Ai docs](https://ai.baidu.com/docs)

主要采用[自定义模版文字识别](https://ai.baidu.com/docs#/iOCR-General-API/top)，上传编辑[模版](https://ai.baidu.com/iocr#/templatelist)，配置模版id：
```
baidu:
  ai:
    ocr:
      templates:
       - platform: Wechat
         id: 11qq22w343e3e3e3e3e3e3e
       - platform: Alipay
         id: dwdwerewd34r3r3r23e2e22
```
通过参数传递
> POST /rest/2.0/solution/v1/iocr/recognise

```json
{
  image: base64 String,
  templateSign: ${templateId}
}
```
