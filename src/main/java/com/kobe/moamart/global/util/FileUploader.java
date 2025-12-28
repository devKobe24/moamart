package com.kobe.moamart.global.util;

import org.springframework.web.multipart.MultipartFile;

/**
 * packageName    : com.kobe.moamart.global.util
 * fileName       : FileUploader
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */
public interface FileUploader {
    // 파일을 저장하고, 접근 가능한 URL을 반환
    String upload(MultipartFile file);
}
