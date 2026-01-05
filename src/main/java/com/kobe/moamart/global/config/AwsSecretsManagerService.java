package com.kobe.moamart.global.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.kobe.moamart.global.config
 * fileName       : AwsSecretsManagerService
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    : AWS Secrets Manager에서 시크릿을 가져오는 서비스 (운영 환경용)
 */
@Slf4j
@Service
@Profile("prod") // 운영 환경(prod)에서만 활성화
public class AwsSecretsManagerService {

    private final AWSSecretsManager secretsManager;
    private final String region;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> cache = new HashMap<>(); // 캐싱을 위한 Map

    public AwsSecretsManagerService(
            @Value("${aws.secrets-manager.region:ap-northeast-2}") String region
    ) {
        this.region = region;

        // AWS 자격 증명은 DefaultAWSCredentialsProviderChain 사용
        // (환경 변수, IAM 역할 등에서 자동으로 가져옴)
        this.secretsManager = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();

        log.info("AWS Secrets Manager 서비스가 초기화되었습니다. 리전: {}", region);
    }

    /**
     * Secrets Manager에서 시크릿 값을 가져옴 (JSON 형식)
     * @param secretName 시크릿 이름
     * @return JSON 문자열
     */
    public String getSecretString(String secretName) {
        // 캐시 확인
        if (cache.containsKey(secretName)) {
            log.debug("시크릿 캐시에서 가져옴: {}", secretName);
            return cache.get(secretName);
        }

        try {
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                    .withSecretId(secretName);

            GetSecretValueResult getSecretValueResult = secretsManager.getSecretValue(getSecretValueRequest);

            String secret = getSecretValueResult.getSecretString();
            
            // 캐시에 저장
            cache.put(secretName, secret);
            
            log.info("시크릿을 성공적으로 가져왔습니다: {}", secretName);
            return secret;

        } catch (Exception e) {
            log.error("시크릿을 가져오는 중 오류 발생: {}", secretName, e);
            throw new RuntimeException("AWS Secrets Manager에서 시크릿을 가져오는 데 실패했습니다: " + secretName, e);
        }
    }

    /**
     * Secrets Manager에서 시크릿 값을 JSON으로 파싱하여 특정 키의 값을 가져옴
     * @param secretName 시크릿 이름
     * @param key JSON 키
     * @return 키에 해당하는 값
     */
    public String getSecretValue(String secretName, String key) {
        try {
            String secretString = getSecretString(secretName);
            JsonNode jsonNode = objectMapper.readTree(secretString);
            
            if (jsonNode.has(key)) {
                return jsonNode.get(key).asText();
            } else {
                throw new RuntimeException("시크릿에 키가 존재하지 않습니다: " + key + " (시크릿: " + secretName + ")");
            }
        } catch (Exception e) {
            log.error("시크릿 값을 파싱하는 중 오류 발생: {} -> {}", secretName, key, e);
            throw new RuntimeException("시크릿 값을 파싱하는 데 실패했습니다: " + secretName + " -> " + key, e);
        }
    }

    /**
     * 캐시 초기화 (새로고침 시 사용)
     */
    public void clearCache() {
        cache.clear();
        log.info("시크릿 캐시가 초기화되었습니다.");
    }

    /**
     * 특정 시크릿의 캐시만 제거
     */
    public void clearCache(String secretName) {
        cache.remove(secretName);
        log.debug("시크릿 캐시가 제거되었습니다: {}", secretName);
    }
}

