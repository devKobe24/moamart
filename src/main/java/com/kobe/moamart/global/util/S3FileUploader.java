package com.kobe.moamart.global.util;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * packageName    : com.kobe.moamart.global.util
 * fileName       : S3FileUploader
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    : AWS S3를 사용한 파일 업로드 (운영 환경용)
 *                   - 버킷: moamart-product-images-bucket
 *                   - 저장 경로: images/ 디렉토리 안에 모든 상품 이미지 저장
 */
@Slf4j
@Component
@Profile("prod") // 운영 환경(prod)에서만 활성화
public class S3FileUploader implements FileUploader {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String cloudfrontUrl;

    public S3FileUploader(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.cloudfront-url:}") String cloudfrontUrl
    ) {
        this.bucketName = bucketName;
        this.cloudfrontUrl = cloudfrontUrl;

        // IAM 역할 사용 (Secrets Manager에서 자격 증명을 찾지 않음)
        // DefaultAWSCredentialsProviderChain이 자동으로 EC2 IAM 역할을 사용
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();

        log.info("S3 클라이언트가 EC2 IAM 역할을 사용하여 초기화되었습니다. 버킷: {}, 리전: {}", bucketName, region);
    }

    @Override
    public String upload(MultipartFile file) {
        return upload(file, false);
    }

    @Override
    public String upload(MultipartFile file, boolean isThumbnail) {
        if (file == null || file.isEmpty()) {
            log.warn("업로드할 파일이 비어있습니다.");
            return null;
        }

        try {
            log.debug("이미지 업로드 시작: {}, 썸네일: {}", file.getOriginalFilename(), isThumbnail);
            
            // 1. 이미지 최적화 (리사이징 및 압축)
            byte[] optimizedImageBytes;
            
            if (ImageOptimizer.isImageFile(file)) {
                try {
                    optimizedImageBytes = ImageOptimizer.optimizeImage(file, isThumbnail);
                    log.debug("이미지 최적화 완료: {} bytes", optimizedImageBytes.length);
                } catch (Exception e) {
                    log.error("이미지 최적화 실패: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("이미지 최적화 실패: " + file.getOriginalFilename(), e);
                }
            } else {
                // 이미지가 아닌 경우 원본 그대로
                optimizedImageBytes = file.getBytes();
                log.debug("이미지가 아니므로 원본 그대로 사용: {} bytes", optimizedImageBytes.length);
            }

            // 2. 고유한 파일명 생성 (UUID + 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg"; // 최적화 후 기본적으로 JPEG
            if (originalFilename != null && originalFilename.contains(".")) {
                String originalExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                // PNG는 PNG로 유지, 나머지는 JPEG로 변환
                if (originalExtension.equalsIgnoreCase(".png")) {
                    extension = ".png";
                }
            }

            String storeFilename = UUID.randomUUID() + extension;

            // 3. S3에 저장할 경로: moamart-product-images-bucket/images/파일명
            // images/ 디렉토리 안에 모든 상품 이미지 저장
            String s3Key = "images/" + storeFilename;
            log.debug("S3 키: {}", s3Key);

            // 4. 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = extension.equals(".png") ? "image/png" : "image/jpeg";
            }
            metadata.setContentType(contentType);
            metadata.setContentLength(optimizedImageBytes.length);

            // 5. S3에 최적화된 이미지 업로드
            // ACL은 사용하지 않음 (버킷이 ACL을 허용하지 않을 수 있음)
            // 대신 버킷 정책으로 공개 읽기 권한 관리
            try (InputStream inputStream = new ByteArrayInputStream(optimizedImageBytes)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucketName,
                        s3Key,
                        inputStream,
                        metadata
                );
                // ACL 제거: .withCannedAcl(CannedAccessControlList.PublicRead) 
                // 버킷 정책으로 공개 읽기 권한 설정 필요

                s3Client.putObject(putObjectRequest);
                log.info("S3 업로드 성공: {}", s3Key);
            } catch (Exception e) {
                log.error("S3 업로드 실패: 버킷={}, 키={}", bucketName, s3Key, e);
                throw new RuntimeException("S3 파일 업로드 실패: " + file.getOriginalFilename() + " (버킷: " + bucketName + ", 키: " + s3Key + ")", e);
            }

            // 6. 접근 가능한 URL 반환
            String url;
            if (cloudfrontUrl != null && !cloudfrontUrl.isEmpty()) {
                url = cloudfrontUrl + (cloudfrontUrl.endsWith("/") ? "" : "/") + s3Key;
            } else {
                url = s3Client.getUrl(bucketName, s3Key).toString();
            }
            log.info("업로드된 이미지 URL: {}", url);
            log.info("이미지 접근 테스트: 브라우저에서 다음 URL을 직접 열어보세요: {}", url);
            return url;
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("S3 파일 업로드 실패: " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            log.error("S3 파일 업로드 중 예상치 못한 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("S3 파일 업로드 실패: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        try {
            // URL에서 S3 key 추출
            String s3Key = extractS3KeyFromUrl(url);
            
            if (s3Key == null || s3Key.isEmpty()) {
                return; // 유효하지 않은 URL이면 무시
            }

            // S3에서 파일 삭제
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, s3Key);
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            // 삭제 실패해도 로그만 남기고 예외는 던지지 않음 (무시)
            System.err.println("S3 파일 삭제 실패: " + url + ", 오류: " + e.getMessage());
        }
    }

    /**
     * URL에서 S3 key 추출
     * CloudFront URL 또는 S3 URL에서 key를 추출
     */
    private String extractS3KeyFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            
            // CloudFront URL인 경우: /images/파일명
            // S3 URL인 경우: /버킷명/images/파일명 또는 /images/파일명
            
            if (path.startsWith("/images/")) {
                return path.substring(1); // 앞의 / 제거
            }
            
            // S3 URL에서 버킷명 뒤의 경로 추출
            if (uri.getHost().contains("s3")) {
                // https://버킷명.s3.ap-northeast-2.amazonaws.com/images/파일명
                // 또는 https://s3.ap-northeast-2.amazonaws.com/버킷명/images/파일명
                if (path.startsWith("/" + bucketName + "/")) {
                    return path.substring(bucketName.length() + 2); // /버킷명/ 제거
                }
            }
            
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (URISyntaxException e) {
            System.err.println("URL 파싱 실패: " + url);
            return null;
        }
    }
}

