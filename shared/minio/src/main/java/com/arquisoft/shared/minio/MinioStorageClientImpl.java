package com.arquisoft.shared.minio;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;
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

    private static final String CODIGO_OBJETO_INEXISTENTE = "NoSuchKey";

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
            log.error(Mensajes.obtener(AlmacenamientoKey.LOG_URL_CARGA_FALLIDA), bucket, objectKey, e);
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_URL_CARGA),
                    AppCodes.Minio.URL_CARGA_FALLIDA, e);
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
            log.error(Mensajes.obtener(AlmacenamientoKey.LOG_URL_DESCARGA_FALLIDA), bucket, objectKey, e);
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_URL_DESCARGA),
                    AppCodes.Minio.URL_DESCARGA_FALLIDA, e);
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
            log.error(Mensajes.obtener(AlmacenamientoKey.LOG_ELIMINACION_FALLIDA), bucket, objectKey, e);
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_ELIMINACION),
                    AppCodes.Minio.ELIMINACION_FALLIDA, e);
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
            if (CODIGO_OBJETO_INEXISTENTE.equals(e.errorResponse().code())) {
                return false;
            }
            throw errorDeVerificacion(e);
        } catch (Exception e) {
            throw errorDeVerificacion(e);
        }
    }

    private MinioOperationException errorDeVerificacion(Exception causa) {
        return new MinioOperationException(
                Mensajes.obtener(AlmacenamientoKey.ERROR_VERIFICACION),
                AppCodes.Minio.VERIFICACION_FALLIDA, causa);
    }

    private void ensureBucketExists(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info(Mensajes.obtener(AlmacenamientoKey.LOG_BUCKET_CREADO), bucket);
        }
    }
}
