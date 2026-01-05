package com.kobe.moamart.global.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * packageName    : com.kobe.moamart.global.util
 * fileName       : ImageOptimizer
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    : 이미지 리사이징 및 최적화 유틸리티
 */
public class ImageOptimizer {

    // 썸네일 이미지 최대 크기 (가로, 세로)
    private static final int THUMBNAIL_MAX_WIDTH = 800;
    private static final int THUMBNAIL_MAX_HEIGHT = 800;
    
    // 원본 이미지 최대 크기 (가로, 세로) - 원본이 너무 크면 리사이징
    private static final int ORIGINAL_MAX_WIDTH = 1920;
    private static final int ORIGINAL_MAX_HEIGHT = 1920;
    
    // JPEG 품질 (0.0 ~ 1.0, 낮을수록 압축률 높음)
    private static final double JPEG_QUALITY = 0.85;

    /**
     * 이미지를 최적화 (리사이징 및 압축)
     * @param file 원본 파일
     * @param isThumbnail 썸네일 여부 (true면 작은 크기로, false면 원본 크기 제한)
     * @return 최적화된 이미지 바이트 배열
     */
    public static byte[] optimizeImage(MultipartFile file, boolean isThumbnail) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            
            if (originalImage == null) {
                throw new IOException("이미지 파일을 읽을 수 없습니다: " + file.getOriginalFilename());
            }

            int maxWidth = isThumbnail ? THUMBNAIL_MAX_WIDTH : ORIGINAL_MAX_WIDTH;
            int maxHeight = isThumbnail ? THUMBNAIL_MAX_HEIGHT : ORIGINAL_MAX_HEIGHT;

            // 이미지 크기 확인
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // 이미지가 최대 크기보다 작으면 리사이징하지 않음 (품질 유지)
            if (width <= maxWidth && height <= maxHeight) {
                // 이미지 형식에 따라 최적화만 수행
                return compressImage(originalImage, file.getContentType());
            }

            // 비율 유지하며 리사이징
            BufferedImage resizedImage = Thumbnails.of(originalImage)
                    .size(maxWidth, maxHeight)
                    .keepAspectRatio(true)
                    .asBufferedImage();

            // 압축
            return compressImage(resizedImage, file.getContentType());
        }
    }

    /**
     * 이미지 압축
     */
    private static byte[] compressImage(BufferedImage image, String contentType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        String formatName = getFormatName(contentType);
        
        if ("jpeg".equals(formatName) || "jpg".equals(formatName)) {
            // JPEG 압축 (품질 설정)
            Thumbnails.of(image)
                    .scale(1.0)
                    .outputFormat("jpg")
                    .outputQuality(JPEG_QUALITY)
                    .toOutputStream(baos);
        } else if ("png".equals(formatName)) {
            // PNG는 손실 압축 없이 저장 (품질 유지)
            ImageIO.write(image, "png", baos);
        } else {
            // 기타 형식은 JPEG로 변환하여 저장
            Thumbnails.of(image)
                    .scale(1.0)
                    .outputFormat("jpg")
                    .outputQuality(JPEG_QUALITY)
                    .toOutputStream(baos);
        }

        return baos.toByteArray();
    }

    /**
     * Content-Type에서 이미지 형식 추출
     */
    private static String getFormatName(String contentType) {
        if (contentType == null) {
            return "jpg"; // 기본값
        }

        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            return "jpg";
        } else if (contentType.contains("png")) {
            return "png";
        } else if (contentType.contains("gif")) {
            return "gif";
        } else if (contentType.contains("webp")) {
            return "webp";
        }

        return "jpg"; // 기본값
    }

    /**
     * 이미지가 이미지 파일인지 확인
     */
    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}

