package com.kobe.moamart.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * packageName    : com.kobe.moamart.global.util
 * fileName       : LocalFileUploader
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */
@Component
@Profile("dev") // 개발 환경(dev)에서만 활성화
public class LocalFileUploader implements FileUploader {

    // application.yml에서 경로를 주입받음 (없으면 기본값 사용)
    @Value("${file.upload-dir:./uploads/}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file) {
        return upload(file, false);
    }

    @Override
    public String upload(MultipartFile file, boolean isThumbnail) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 1. 폴더 생성 (없으면 자동 생성)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 2. 이미지 최적화 (리사이징 및 압축)
            byte[] optimizedImageBytes;
            String extension = ".jpg";
            
            if (ImageOptimizer.isImageFile(file)) {
                optimizedImageBytes = ImageOptimizer.optimizeImage(file, isThumbnail);
                
                // 확장자 결정
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null && originalFilename.contains(".")) {
                    String originalExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    if (originalExtension.equalsIgnoreCase(".png")) {
                        extension = ".png";
                    }
                }
            } else {
                // 이미지가 아닌 경우 원본 그대로
                optimizedImageBytes = file.getBytes();
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
            }

            // 3. 파일명 중복 방지 (UUID)
            String storeFilename = UUID.randomUUID() + extension;
            String fullPath = uploadDir + storeFilename;

            // 4. 최적화된 파일 저장
            try (FileOutputStream fos = new FileOutputStream(fullPath)) {
                fos.write(optimizedImageBytes);
            }

            // 5. 웹 접근 URL 반환 (/images/파일명)
            return "/images/" + storeFilename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        try {
            // URL에서 파일명 추출 (/images/파일명 -> 파일명)
            String filename = extractFilenameFromUrl(url);
            
            if (filename == null || filename.isEmpty()) {
                return; // 유효하지 않은 URL이면 무시
            }

            // 파일 삭제
            String fullPath = uploadDir + filename;
            File file = new File(fullPath);
            
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("파일 삭제 실패: " + fullPath);
                }
            }
        } catch (Exception e) {
            // 삭제 실패해도 로그만 남기고 예외는 던지지 않음 (무시)
            System.err.println("로컬 파일 삭제 실패: " + url + ", 오류: " + e.getMessage());
        }
    }

    /**
     * URL에서 파일명 추출
     * /images/파일명 -> 파일명
     */
    private String extractFilenameFromUrl(String url) {
        if (url.startsWith("/images/")) {
            return url.substring("/images/".length());
        }
        // 전체 경로가 URL에 포함된 경우
        if (url.contains("/images/")) {
            return url.substring(url.lastIndexOf("/images/") + "/images/".length());
        }
        return null;
    }
}
