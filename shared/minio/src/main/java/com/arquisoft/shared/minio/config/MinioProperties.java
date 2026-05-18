package com.arquisoft.shared.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Expiry presignedUrlExpiry = new Expiry();

    @Data
    public static class Expiry {
        private int uploadMinutes = 15;
        private int downloadMinutes = 15;
    }
}
