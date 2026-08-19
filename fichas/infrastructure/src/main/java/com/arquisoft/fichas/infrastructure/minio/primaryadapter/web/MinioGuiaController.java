package com.arquisoft.fichas.infrastructure.minio.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.message.key.fichas.MinioGuiaKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.minio.MinioStorageClient;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${rutas.fichas.minio-guia.base:/fichas/minio/guia}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.MinioGuia.TAG_NAME, description = FichasApiMessages.MinioGuia.TAG_DESCRIPTION)
public class MinioGuiaController {

    private static final String AUTENTICADO = "isAuthenticated()";

    // Claves del JSON de respuesta: son contrato con el cliente, no texto traducible.
    private static final String CAMPO_BUCKET = "bucket";
    private static final String CAMPO_KEY = "key";
    private static final String CAMPO_METHOD = "method";
    private static final String CAMPO_URL = "url";
    private static final String CAMPO_EXISTE = "existe";

    private static final String METODO_CARGA = "PUT";
    private static final String METODO_DESCARGA = "GET";

    private final MinioStorageClient minioStorageClient;
    private final AppLogger logger;

    @GetMapping("${rutas.fichas.minio-guia.upload-url:/upload-url}")
    @PreAuthorize(AUTENTICADO)
    @Operation(
            summary = FichasApiMessages.MinioGuia.CARGA_SUMMARY,
            description = FichasApiMessages.MinioGuia.CARGA_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponse(responseCode = ApiCodes.OK, description = FichasApiMessages.MinioGuia.CARGA_RESP_200)
    public ResponseEntity<Map<String, String>> generarUrlCarga(
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_BUCKET)
            @RequestParam String bucket,
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_KEY)
            @RequestParam String key) {

        logger.debug(Mensajes.obtener(MinioGuiaKey.LOG_UPLOAD_URL), bucket, key);
        var url = minioStorageClient.generateUploadPresignedUrl(bucket, key);
        return ResponseEntity.ok(Map.of(
                CAMPO_BUCKET, bucket,
                CAMPO_KEY, key,
                CAMPO_METHOD, METODO_CARGA,
                CAMPO_URL, url
        ));
    }

    @GetMapping("${rutas.fichas.minio-guia.download-url:/download-url}")
    @PreAuthorize(AUTENTICADO)
    @Operation(
            summary = FichasApiMessages.MinioGuia.DESCARGA_SUMMARY,
            description = FichasApiMessages.MinioGuia.DESCARGA_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponse(responseCode = ApiCodes.OK, description = FichasApiMessages.MinioGuia.DESCARGA_RESP_200)
    public ResponseEntity<Map<String, String>> generarUrlDescarga(
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_BUCKET)
            @RequestParam String bucket,
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_KEY)
            @RequestParam String key) {

        logger.debug(Mensajes.obtener(MinioGuiaKey.LOG_DOWNLOAD_URL), bucket, key);
        var url = minioStorageClient.generateDownloadPresignedUrl(bucket, key);
        return ResponseEntity.ok(Map.of(
                CAMPO_BUCKET, bucket,
                CAMPO_KEY, key,
                CAMPO_METHOD, METODO_DESCARGA,
                CAMPO_URL, url
        ));
    }

    @GetMapping("${rutas.fichas.minio-guia.existe:/existe}")
    @PreAuthorize(AUTENTICADO)
    @Operation(
            summary = FichasApiMessages.MinioGuia.EXISTE_SUMMARY,
            description = FichasApiMessages.MinioGuia.EXISTE_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponse(responseCode = ApiCodes.OK, description = FichasApiMessages.MinioGuia.EXISTE_RESP_200)
    public ResponseEntity<Map<String, Object>> verificarExistencia(
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_BUCKET)
            @RequestParam String bucket,
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_KEY)
            @RequestParam String key) {

        var existe = minioStorageClient.objectExists(bucket, key);
        return ResponseEntity.ok(Map.of(CAMPO_BUCKET, bucket, CAMPO_KEY, key, CAMPO_EXISTE, existe));
    }

    @DeleteMapping("${rutas.fichas.minio-guia.objeto:/objeto}")
    @PreAuthorize(AUTENTICADO)
    @Operation(
            summary = FichasApiMessages.MinioGuia.ELIMINAR_SUMMARY,
            description = FichasApiMessages.MinioGuia.ELIMINAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponse(responseCode = ApiCodes.NO_CONTENT, description = FichasApiMessages.MinioGuia.ELIMINAR_RESP_204)
    public ResponseEntity<Void> eliminarObjeto(
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_BUCKET)
            @RequestParam String bucket,
            @Parameter(description = FichasApiMessages.MinioGuia.PARAM_KEY)
            @RequestParam String key) {

        logger.debug(Mensajes.obtener(MinioGuiaKey.LOG_DELETE), bucket, key);
        minioStorageClient.deleteObject(bucket, key);
        return ResponseEntity.noContent().build();
    }
}
