package com.arquisoft.fichas.infrastructure.minio.primaryadapter.web;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.minio.MinioStorageClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioGuiaControllerTest {

    @Mock
    private MinioStorageClient minioStorageClient;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private MinioGuiaController minioGuiaController;

    @Test
    void debeGenerarUrlCarga_cuandoParametrosValidos() {
        // Arrange
        String bucket = "arquisoft-fichas";
        String key = "documentos/archivo.pdf";
        String urlEsperada = "https://minio.example.com/presigned-upload-url";
        when(minioStorageClient.generateUploadPresignedUrl(bucket, key)).thenReturn(urlEsperada);

        // Act
        ResponseEntity<Map<String, String>> respuesta = minioGuiaController.generarUrlCarga(bucket, key);

        // Assert
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().get("bucket")).isEqualTo(bucket);
        assertThat(respuesta.getBody().get("key")).isEqualTo(key);
        assertThat(respuesta.getBody().get("method")).isEqualTo("PUT");
        assertThat(respuesta.getBody().get("url")).isEqualTo(urlEsperada);
        verify(minioStorageClient).generateUploadPresignedUrl(bucket, key);
    }

    @Test
    void debeGenerarUrlDescarga_cuandoParametrosValidos() {
        // Arrange
        String bucket = "arquisoft-fichas";
        String key = "documentos/archivo.pdf";
        String urlEsperada = "https://minio.example.com/presigned-download-url";
        when(minioStorageClient.generateDownloadPresignedUrl(bucket, key)).thenReturn(urlEsperada);

        // Act
        ResponseEntity<Map<String, String>> respuesta = minioGuiaController.generarUrlDescarga(bucket, key);

        // Assert
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().get("bucket")).isEqualTo(bucket);
        assertThat(respuesta.getBody().get("key")).isEqualTo(key);
        assertThat(respuesta.getBody().get("method")).isEqualTo("GET");
        assertThat(respuesta.getBody().get("url")).isEqualTo(urlEsperada);
        verify(minioStorageClient).generateDownloadPresignedUrl(bucket, key);
    }

    @Test
    void debeVerificarExistencia_cuandoObjetoExiste() {
        // Arrange
        String bucket = "arquisoft-fichas";
        String key = "documentos/archivo.pdf";
        when(minioStorageClient.objectExists(bucket, key)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> respuesta = minioGuiaController.verificarExistencia(bucket, key);

        // Assert
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().get("bucket")).isEqualTo(bucket);
        assertThat(respuesta.getBody().get("key")).isEqualTo(key);
        assertThat(respuesta.getBody().get("existe")).isEqualTo(true);
        verify(minioStorageClient).objectExists(bucket, key);
    }

    @Test
    void debeVerificarExistencia_cuandoObjetoNoExiste() {
        // Arrange
        String bucket = "arquisoft-fichas";
        String key = "documentos/inexistente.pdf";
        when(minioStorageClient.objectExists(bucket, key)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> respuesta = minioGuiaController.verificarExistencia(bucket, key);

        // Assert
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().get("existe")).isEqualTo(false);
        verify(minioStorageClient).objectExists(bucket, key);
    }

    @Test
    void debeEliminarObjeto_cuandoParametrosValidos() {
        // Arrange
        String bucket = "arquisoft-fichas";
        String key = "documentos/archivo.pdf";

        // Act
        ResponseEntity<Void> respuesta = minioGuiaController.eliminarObjeto(bucket, key);

        // Assert
        assertThat(respuesta.getStatusCode().value()).isEqualTo(204);
        assertThat(respuesta.getBody()).isNull();
        verify(minioStorageClient).deleteObject(bucket, key);
    }
}
