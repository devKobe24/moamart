package com.kobe.moamart.global.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.kobe.moamart.global.config.AwsSecretsManagerService;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
 */
@Component
@Profile("prod") // 운영 환경(prod)에서만 활성화
public class S3FileUploader implements FileUploader {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String cloudfrontUrl;

    public S3FileUploader(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.secrets-manager.secret-name:moamart/aws-credentials}") String secretName,
            @Value("${aws.s3.cloudfront-url:}") String cloudfrontUrl,
            AwsSecretsManagerService secretsManagerService
    ) {
        this.bucketName = bucketName;
        this.cloudfrontUrl = cloudfrontUrl;

        // Secrets Manager에서 자격 증명 가져오기
        String accessKey;
        String secretKey;
        
        try {
            accessKey = secretsManagerService.getSecretValue(secretName, "accessKey");
            secretKey = secretsManagerService.getSecretValue(secretName, "secretKey");
        } catch (Exception e) {
            // Secrets Manager 사용 실패 시 환경 변수에서 가져오기 (하위 호환성)
            System.err.println("Secrets Manager에서 자격 증명을 가져오는데 실패했습니다. 환경 변수를 사용합니다: " + e.getMessage());
            accessKey = System.getenv("AWS_ACCESS_KEY_ID");
            secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
            
            if (accessKey == null || secretKey == null) {
                // 환경 변수도 없으면 DefaultAWSCredentialsProviderChain 사용 (EC2 IAM 역할 등)
                this.s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                        .build();
                return;
            }
        }

        // AWS 자격 증명 설정
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // S3 클라이언트 생성
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

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
            // 1. 이미지 최적화 (리사이징 및 압축)
            byte[] optimizedImageBytes;
            
            if (ImageOptimizer.isImageFile(file)) {
                optimizedImageBytes = ImageOptimizer.optimizeImage(file, isThumbnail);
            } else {
                // 이미지가 아닌 경우 원본 그대로
                optimizedImageBytes = file.getBytes();
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

            // 3. S3에 저장할 경로
            String s3Key = "images/" + storeFilename;

            // 4. 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg".equals(file.getContentType()) || 
                                   file.getContentType() == null ? "image/jpeg" : file.getContentType());
            metadata.setContentLength(optimizedImageBytes.length);

            // 5. S3에 최적화된 이미지 업로드
            try (InputStream inputStream = new ByteArrayInputStream(optimizedImageBytes)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucketName,
                        s3Key,
                        inputStream,
                        metadata
                ).withCannedAcl(CannedAccessControlList.PublicRead); // 공개 읽기 권한

                s3Client.putObject(putObjectRequest);
            }

            // 6. 접근 가능한 URL 반환
            if (cloudfrontUrl != null && !cloudfrontUrl.isEmpty()) {
                return cloudfrontUrl + (cloudfrontUrl.endsWith("/") ? "" : "/") + s3Key;
            } else {
                return s3Client.getUrl(bucketName, s3Key).toString();
            }
        } catch (IOException e) {
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

