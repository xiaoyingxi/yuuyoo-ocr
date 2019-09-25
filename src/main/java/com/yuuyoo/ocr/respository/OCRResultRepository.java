package com.yuuyoo.ocr.respository;

import com.yuuyoo.ocr.entity.OCRResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OCRResultRepository extends MongoRepository<OCRResult, String> {

  public OCRResult findByImageHash(Integer imageHash);

}
