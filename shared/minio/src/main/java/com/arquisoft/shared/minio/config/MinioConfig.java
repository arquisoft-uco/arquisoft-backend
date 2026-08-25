package com.arquisoft.shared.minio.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties properties) throws Exception {
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey());

        if (properties.isTrustSelfSignedCertificates()) {
            builder.httpClient(buildTrustAllClient());
        }

        return builder.build();
    }

    private OkHttpClient buildTrustAllClient() throws Exception {
        X509TrustManager trustAll = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustAll}, null);

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustAll)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }
}
