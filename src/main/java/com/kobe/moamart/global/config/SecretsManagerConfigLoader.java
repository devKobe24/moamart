package com.kobe.moamart.global.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.kobe.moamart.global.config
 * fileName       : SecretsManagerConfigLoader
 * author         : kobe
 * date           : 2026. 01. 06.
 * description    : AWS Secrets Manager에서 값을 가져와서 Spring Environment에 주입
 *                  애플리케이션 시작 전에 실행되므로 DataSource 설정 등에 사용 가능
 */
@Slf4j
public class SecretsManagerConfigLoader implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String SECRET_NAME_PROPERTY = "aws.secrets-manager.secret-name";
    private static final String DEFAULT_SECRET_NAME = "prod/moamart-secrets";
    private static final String DEFAULT_REGION = "ap-northeast-2";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        
        // prod 프로파일이 활성화되어 있지 않으면 건너뛰기
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProdProfile = false;
        for (String profile : activeProfiles) {
            if ("prod".equals(profile)) {
                isProdProfile = true;
                break;
            }
        }
        
        if (!isProdProfile) {
            log.debug("prod 프로파일이 활성화되지 않았습니다. Secrets Manager 로딩을 건너뜁니다.");
            return;
        }

        try {
            String secretName = environment.getProperty(SECRET_NAME_PROPERTY, DEFAULT_SECRET_NAME);
            String region = environment.getProperty("aws.secrets-manager.region", DEFAULT_REGION);
            
            log.info("Secrets Manager에서 시크릿을 가져옵니다: {} (리전: {})", secretName, region);

            // AWS Secrets Manager 클라이언트 생성
            AWSSecretsManager secretsManager = AWSSecretsManagerClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();

            // 시크릿 값 가져오기
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                    .withSecretId(secretName);

            GetSecretValueResult getSecretValueResult = secretsManager.getSecretValue(getSecretValueRequest);
            String secretString = getSecretValueResult.getSecretString();

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(secretString);

            // Spring Environment에 주입할 프로퍼티 맵 생성
            Map<String, Object> properties = new HashMap<>();

            // Secrets Manager에서 가져온 값들을 프로퍼티로 변환
            jsonNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                String value = entry.getValue().asText();
                
                // DATASOURCE_URL, DB_USERNAME, DB_PASSWORD 등의 값을 시스템 프로퍼티에 설정
                if ("DATASOURCE_URL".equals(key)) {
                    properties.put("spring.datasource.url", value);
                } else if ("DB_USERNAME".equals(key)) {
                    properties.put("spring.datasource.username", value);
                } else if ("DB_PASSWORD".equals(key)) {
                    properties.put("spring.datasource.password", value);
                } else {
                    // 기타 키는 그대로 환경 변수처럼 사용할 수 있도록 설정
                    // 예: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY 등
                    properties.put(key.toLowerCase().replace("_", "."), value);
                    // 환경 변수 형식으로도 사용 가능하도록
                    System.setProperty(key, value);
                }
            });

            // Spring Environment에 프로퍼티 소스 추가
            if (!properties.isEmpty()) {
                MutablePropertySources propertySources = environment.getPropertySources();
                propertySources.addFirst(new MapPropertySource("secretsManager", properties));
                log.info("Secrets Manager에서 {} 개의 프로퍼티를 로드했습니다.", properties.size());
                
                // 로드된 프로퍼티 로깅 (보안상 값은 마스킹)
                properties.keySet().forEach(key -> {
                    String value = String.valueOf(properties.get(key));
                    if (key.contains("password") || key.contains("secret") || key.contains("key")) {
                        log.debug("{} = ****", key);
                    } else {
                        log.debug("{} = {}", key, value.length() > 50 ? value.substring(0, 50) + "..." : value);
                    }
                });
            }

        } catch (Exception e) {
            log.warn("Secrets Manager에서 값을 가져오는 데 실패했습니다. 환경 변수나 기본값을 사용합니다: {}", e.getMessage());
            log.debug("Secrets Manager 로딩 실패 상세:", e);
        }
    }
}
