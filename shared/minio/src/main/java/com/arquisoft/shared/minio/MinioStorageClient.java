package com.arquisoft.shared.minio;

public interface MinioStorageClient {

    /** Genera una presigned PUT URL para que el cliente suba el archivo directamente a MinIO. */
    String generateUploadPresignedUrl(String bucket, String objectKey);

    /** Genera una presigned GET URL para que el cliente descargue el archivo directamente de MinIO. */
    String generateDownloadPresignedUrl(String bucket, String objectKey);

    /** Elimina un objeto del bucket (útil para rollbacks o limpiezas). */
    void deleteObject(String bucket, String objectKey);

    /** Verifica si el objeto existe en el bucket. */
    boolean objectExists(String bucket, String objectKey);
}
