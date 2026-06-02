package com.arquisoft.shared.minio;

import com.arquisoft.shared.minio.config.MinioProperties;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioStorageClientImpl implements MinioStorageClient {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public String generateUploadPresignedUrl(String bucket, String objectKey) {
        try {
            ensureBucketExists(bucket);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(properties.getPresignedUrlExpiry().getUploadMinutes(), TimeUnit.MINUTES)
                            .build());
        } catch (MinioOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generando presigned upload URL para {}/{}", bucket, objectKey, e);
            throw new MinioOperationException("No se pudo generar la URL de carga", e);
        }
    }

    @Override
    public String generateDownloadPresignedUrl(String bucket, String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(properties.getPresignedUrlExpiry().getDownloadMinutes(), TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            log.error("Error generando presigned download URL para {}/{}", bucket, objectKey, e);
            throw new MinioOperationException("No se pudo generar la URL de descarga", e);
        }
    }

    @Override
    public void deleteObject(String bucket, String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build());
        } catch (Exception e) {
            log.error("Error eliminando objeto {}/{}", bucket, objectKey, e);
            throw new MinioOperationException("No se pudo eliminar el objeto", e);
        }
    }

    @Override
    public boolean objectExists(String bucket, String objectKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            throw new MinioOperationException("Error verificando existencia del objeto", e);
        } catch (Exception e) {
            throw new MinioOperationException("Error verificando existencia del objeto", e);
        }
    }

    private void ensureBucketExists(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("Bucket '{}' creado automaticamente", bucket);
        }
    }
}
