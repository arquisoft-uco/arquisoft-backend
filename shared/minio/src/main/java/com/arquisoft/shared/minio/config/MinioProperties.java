package com.arquisoft.shared.minio.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties implements InitializingBean {

    private static final int DESCARGA_MINUTOS_MINIMO = 1;
    private static final int DESCARGA_MINUTOS_MAXIMO = 5;

    private String endpoint;
    private String endpointPublico;
    private String accessKey;
    private String secretKey;
    private String region = "us-east-1";
    private Expiry presignedUrlExpiry = new Expiry();

    @Override
    public void afterPropertiesSet() {
        var minutos = presignedUrlExpiry.getDescargaMinutos();
        if (minutos < DESCARGA_MINUTOS_MINIMO || minutos > DESCARGA_MINUTOS_MAXIMO) {
            throw new IllegalStateException("minio.presigned-url-expiry.descarga-minutos debe estar entre "
                    + DESCARGA_MINUTOS_MINIMO + " y " + DESCARGA_MINUTOS_MAXIMO + " y es " + minutos);
        }
    }

    @Data
    public static class Expiry {
        private int formularioCargaMinutos = 15;
        private int descargaMinutos = 5;
    }
}
