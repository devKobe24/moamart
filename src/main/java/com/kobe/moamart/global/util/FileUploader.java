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
    // 파일을 저장하고, 접근 가능한 URL을 반환 (리사이징 및 최적화 포함)
    String upload(MultipartFile file);
    
    // 파일을 저장하고, 접근 가능한 URL을 반환 (썸네일 여부 지정)
    // isThumbnail이 true면 작은 크기로 리사이징, false면 원본 크기 제한 내에서 리사이징
    default String upload(MultipartFile file, boolean isThumbnail) {
        // 기본 구현은 기존 upload 메서드 호출 (하위 호환성)
        return upload(file);
    }
    
    // URL을 기반으로 파일 삭제
    void delete(String url);
}
