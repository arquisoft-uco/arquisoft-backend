package com.arquisoft.shared.minio;

public interface MinioStorageClient {

    String generateUploadPresignedUrl(String bucket, String objectKey);

    String generateDownloadPresignedUrl(String bucket, String objectKey);

    void deleteObject(String bucket, String objectKey);

    boolean objectExists(String bucket, String objectKey);
}
