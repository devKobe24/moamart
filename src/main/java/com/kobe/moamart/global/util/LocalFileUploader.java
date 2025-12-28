package com.kobe.moamart.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        if (file.isEmpty()) {
            return null; // or throw Exception
        }

        try {
            // 1. 폴더 생성 (없으면 자동 생성)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 2. 파일명 중복 방지 (UUID)
            String originalFilename = file.getOriginalFilename();
            String storeFilename = UUID.randomUUID() + "_" + originalFilename;
            String fullPath = uploadDir + storeFilename;

            // 3. 파일 저장
            file.transferTo(new File(fullPath));

            // 4. 웹 접근 URL 반환 (/images/파일명)
            return "/images/" + storeFilename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }
}
