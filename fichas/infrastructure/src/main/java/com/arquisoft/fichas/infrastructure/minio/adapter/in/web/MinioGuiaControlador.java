package com.arquisoft.fichas.infrastructure.minio.adapter.in.web;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.minio.MinioStorageClient;
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
@Tag(name = "MinIO Guía", description = "Endpoints de prueba para validar el módulo shared:minio. Eliminar tras el PoC.")
public class MinioGuiaControlador {

    private final MinioStorageClient minioStorageClient;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @GetMapping("${rutas.fichas.minio-guia.upload-url:/upload-url}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Generar presigned URL de carga",
            description = "Retorna una URL firmada para subir un archivo directamente a MinIO (PUT). Válida 15 minutos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "URL generada exitosamente")
    public ResponseEntity<Map<String, String>> generarUrlCarga(
            @Parameter(description = "Nombre del bucket destino", example = "arquisoft-fichas")
            @RequestParam String bucket,
            @Parameter(description = "Clave del objeto (ruta + nombre)", example = "documentos/mi-archivo.pdf")
            @RequestParam String key) {

        logger.debug(catalog.obtener(FichasKeys.MinioGuia.LOG_UPLOAD_URL), bucket, key);
        String url = minioStorageClient.generateUploadPresignedUrl(bucket, key);
        return ResponseEntity.ok(Map.of(
                "bucket", bucket,
                "key", key,
                "method", "PUT",
                "url", url
        ));
    }

    @GetMapping("${rutas.fichas.minio-guia.download-url:/download-url}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Generar presigned URL de descarga",
            description = "Retorna una URL firmada para descargar un archivo directamente de MinIO (GET). Válida 15 minutos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "URL generada exitosamente")
    public ResponseEntity<Map<String, String>> generarUrlDescarga(
            @Parameter(description = "Nombre del bucket", example = "arquisoft-fichas")
            @RequestParam String bucket,
            @Parameter(description = "Clave del objeto", example = "documentos/mi-archivo.pdf")
            @RequestParam String key) {

        logger.debug(catalog.obtener(FichasKeys.MinioGuia.LOG_DOWNLOAD_URL), bucket, key);
        String url = minioStorageClient.generateDownloadPresignedUrl(bucket, key);
        return ResponseEntity.ok(Map.of(
                "bucket", bucket,
                "key", key,
                "method", "GET",
                "url", url
        ));
    }

    @GetMapping("${rutas.fichas.minio-guia.existe:/existe}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Verificar si un objeto existe",
            description = "Comprueba si el objeto indicado existe en el bucket.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Resultado de la verificación")
    public ResponseEntity<Map<String, Object>> verificarExistencia(
            @RequestParam String bucket,
            @RequestParam String key) {

        boolean existe = minioStorageClient.objectExists(bucket, key);
        return ResponseEntity.ok(Map.of("bucket", bucket, "key", key, "existe", existe));
    }

    @DeleteMapping("${rutas.fichas.minio-guia.objeto:/objeto}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Eliminar un objeto",
            description = "Elimina el objeto indicado del bucket.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Objeto eliminado")
    public ResponseEntity<Void> eliminarObjeto(
            @RequestParam String bucket,
            @RequestParam String key) {

        logger.debug(catalog.obtener(FichasKeys.MinioGuia.LOG_DELETE), bucket, key);
        minioStorageClient.deleteObject(bucket, key);
        return ResponseEntity.noContent().build();
    }
}
