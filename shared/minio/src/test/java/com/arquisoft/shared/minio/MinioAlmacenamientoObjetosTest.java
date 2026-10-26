package com.arquisoft.shared.minio;

import com.arquisoft.shared.minio.config.MinioProperties;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
import io.minio.CopyObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.credentials.Credentials;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import okhttp3.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioAlmacenamientoObjetosTest {

    private static final String BUCKET = "repositorio-artefactos";

    @Mock
    private MinioClient minioClient;

    private MinioAlmacenamientoObjetos almacenamiento;

    @BeforeEach
    void setUp() {
        var propiedades = new MinioProperties();
        propiedades.setEndpointPublico("https://s3.arquisoft.test/");
        propiedades.getPresignedUrlExpiry().setFormularioCargaMinutos(15);
        almacenamiento = new MinioAlmacenamientoObjetos(minioClient, propiedades);
    }

    private static ErrorResponseException errorS3(String codigo) {
        return new ErrorResponseException(
                new ErrorResponse(codigo, "msg", BUCKET, "obj", "/", "req", "host"), null, "trace");
    }

    @Test
    void debeFirmarPolicyConKeyTipoRangoYMetadatos() throws Exception {
        // Arrange
        when(minioClient.getPresignedPostFormData(any(PostPolicy.class)))
                .thenReturn(Map.of("policy", "p", "x-amz-signature", "s"));
        var metadatos = new LinkedHashMap<String, String>();
        metadatos.put("solicitante", "u-1");
        var solicitud = new SolicitudFormularioCarga(
                BUCKET, "pendientes/c-1", "application/pdf", 5_242_880L, metadatos);

        // Act
        var formulario = almacenamiento.generarFormularioCarga(solicitud);

        // Assert
        var captor = ArgumentCaptor.forClass(PostPolicy.class);
        verify(minioClient).getPresignedPostFormData(captor.capture());
        var datos = captor.getValue().formData(new Credentials("ak", "sk", null, null), "us-east-1");
        var json = new String(Base64.getDecoder().decode(datos.get("policy")), StandardCharsets.UTF_8);
        assertThat(json)
                .contains("[\"eq\",\"$bucket\",\"repositorio-artefactos\"]")
                .contains("[\"eq\",\"$key\",\"pendientes/c-1\"]")
                .contains("[\"eq\",\"$Content-Type\",\"application/pdf\"]")
                .contains("[\"eq\",\"$x-amz-meta-solicitante\",\"u-1\"]")
                .contains("[\"content-length-range\",1,5242880]");
        assertThat(formulario.campos())
                .containsEntry("policy", "p")
                .containsEntry("x-amz-signature", "s")
                .containsEntry("key", "pendientes/c-1")
                .containsEntry("Content-Type", "application/pdf")
                .containsEntry("x-amz-meta-solicitante", "u-1");
        assertThat(formulario.expiraEn()).isAfter(java.time.Instant.now());
    }

    @Test
    void debeCodificarMetadatosNoAscii_enPolicyYCampos() throws Exception {
        // Arrange
        when(minioClient.getPresignedPostFormData(any(PostPolicy.class))).thenReturn(Map.of());
        var solicitud = new SolicitudFormularioCarga(BUCKET, "pendientes/c-1", "application/pdf", 10L,
                Map.of("nombre-archivo", "Guía final.pdf"));

        // Act
        var formulario = almacenamiento.generarFormularioCarga(solicitud);

        // Assert
        var captor = ArgumentCaptor.forClass(PostPolicy.class);
        verify(minioClient).getPresignedPostFormData(captor.capture());
        var datos = captor.getValue().formData(new Credentials("ak", "sk", null, null), "us-east-1");
        var json = new String(Base64.getDecoder().decode(datos.get("policy")), StandardCharsets.UTF_8);
        assertThat(json).contains("[\"eq\",\"$x-amz-meta-nombre-archivo\",\"Gu%C3%ADa+final.pdf\"]");
        assertThat(formulario.campos()).containsEntry("x-amz-meta-nombre-archivo", "Gu%C3%ADa+final.pdf");
    }

    @Test
    void debeUsarEndpointPublicoEnLaUrl() throws Exception {
        // Arrange
        when(minioClient.getPresignedPostFormData(any(PostPolicy.class))).thenReturn(Map.of());

        // Act
        var formulario = almacenamiento.generarFormularioCarga(
                new SolicitudFormularioCarga(BUCKET, "pendientes/c-1", "application/pdf", 10L, Map.of()));

        // Assert
        assertThat(formulario.url()).isEqualTo("https://s3.arquisoft.test/" + BUCKET);
    }

    @Test
    void debeLanzarMinioOperationException_cuandoFallaLaFirma() throws Exception {
        // Arrange
        when(minioClient.getPresignedPostFormData(any(PostPolicy.class))).thenThrow(new IOException("caido"));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.generarFormularioCarga(
                new SolicitudFormularioCarga(BUCKET, "k", "application/pdf", 10L, Map.of())))
                .isInstanceOf(MinioOperationException.class);
    }

    @Test
    void debeConsultarMetadatos_conEtagSinComillasYClavesSinPrefijo() throws Exception {
        // Arrange
        var cabeceras = Headers.of(
                "ETag", "\"abc123\"",
                "Content-Length", "2048",
                "Content-Type", "application/pdf",
                "Last-Modified", "Thu, 15 Oct 2026 10:00:00 GMT",
                "X-Amz-Meta-Solicitante", "u-1",
                "X-Amz-Meta-Nombre-Archivo", "guia.pdf");
        var respuesta = new StatObjectResponse(cabeceras, BUCKET, "us-east-1", "pendientes/c-1");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(respuesta);

        // Act
        var metadatos = almacenamiento.consultarMetadatos(BUCKET, "pendientes/c-1");

        // Assert
        assertThat(metadatos.esVacio()).isFalse();
        assertThat(metadatos.etag()).isEqualTo("abc123");
        assertThat(metadatos.tamano()).isEqualTo(2048L);
        assertThat(metadatos.tipoContenido()).isEqualTo("application/pdf");
        assertThat(metadatos.metadatos())
                .containsEntry("solicitante", "u-1")
                .containsEntry("nombre-archivo", "guia.pdf");
    }

    @Test
    void debeDecodificarMetadatos_cuandoConsulta() throws Exception {
        // Arrange
        var cabeceras = Headers.of(
                "ETag", "\"abc123\"",
                "Content-Length", "10",
                "Content-Type", "application/pdf",
                "Last-Modified", "Thu, 15 Oct 2026 10:00:00 GMT",
                "X-Amz-Meta-Nombre-Archivo", "Gu%C3%ADa+final.pdf");
        var respuesta = new StatObjectResponse(cabeceras, BUCKET, "us-east-1", "pendientes/c-1");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(respuesta);

        // Act
        var metadatos = almacenamiento.consultarMetadatos(BUCKET, "pendientes/c-1");

        // Assert
        assertThat(metadatos.metadatos()).containsEntry("nombre-archivo", "Guía final.pdf");
    }

    @Test
    void debeRetornarVacio_cuandoNoSuchKeyAlConsultar() throws Exception {
        // Arrange
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(errorS3("NoSuchKey"));

        // Act & Assert
        assertThat(almacenamiento.consultarMetadatos(BUCKET, "pendientes/c-1").esVacio()).isTrue();
    }

    @Test
    void debeLanzarMinioOperationException_cuandoOtroErrorS3AlConsultar() throws Exception {
        // Arrange
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(errorS3("AccessDenied"));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.consultarMetadatos(BUCKET, "pendientes/c-1"))
                .isInstanceOf(MinioOperationException.class);
    }

    @Test
    void debeLanzarMinioOperationException_cuandoFallaElTransporteAlConsultar() throws Exception {
        // Arrange
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new IOException("caido"));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.consultarMetadatos(BUCKET, "pendientes/c-1"))
                .isInstanceOf(MinioOperationException.class);
    }

    @Test
    void debeAbrirLecturaConElEtagDelGet() throws Exception {
        // Arrange
        var cuerpo = new ByteArrayInputStream("contenido".getBytes(StandardCharsets.UTF_8));
        var respuesta = new GetObjectResponse(Headers.of("ETag", "\"abc123\""), BUCKET, "r", "o", cuerpo);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(respuesta);

        // Act
        var lectura = almacenamiento.abrirLectura(BUCKET, "pendientes/c-1");

        // Assert
        assertThat(lectura).isPresent();
        try (var objeto = lectura.get()) {
            assertThat(objeto.etag()).isEqualTo("abc123");
            assertThat(new String(objeto.contenido().readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("contenido");
        }
    }

    @Test
    void debeRetornarVacio_cuandoNoSuchKeyAlLeer() throws Exception {
        // Arrange
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(errorS3("NoSuchKey"));

        // Act & Assert
        assertThat(almacenamiento.abrirLectura(BUCKET, "pendientes/c-1")).isEmpty();
    }

    @Test
    void debeLanzarMinioOperationException_cuandoFallaLaLectura() throws Exception {
        // Arrange
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(errorS3("InternalError"));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.abrirLectura(BUCKET, "pendientes/c-1"))
                .isInstanceOf(MinioOperationException.class);
    }

    @Test
    void debeCopiarCondicionadoAlEtag() throws Exception {
        // Act
        almacenamiento.copiar(BUCKET, "pendientes/c-1", "versiones/v-1", "\"abc123\"");

        // Assert
        var captor = ArgumentCaptor.forClass(CopyObjectArgs.class);
        verify(minioClient).copyObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo(BUCKET);
        assertThat(captor.getValue().object()).isEqualTo("versiones/v-1");
        assertThat(captor.getValue().source().object()).isEqualTo("pendientes/c-1");
        assertThat(captor.getValue().source().matchETag()).isEqualTo("abc123");
    }

    @Test
    void debeLanzarMinioOperationException_cuandoLaCopiaCondicionadaSeRechaza() throws Exception {
        // Arrange
        when(minioClient.copyObject(any(CopyObjectArgs.class))).thenThrow(errorS3("PreconditionFailed"));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.copiar(BUCKET, "pendientes/c-1", "versiones/v-1", "abc"))
                .isInstanceOf(MinioOperationException.class);
    }

    @Test
    void debeEliminar() throws Exception {
        // Act
        almacenamiento.eliminar(BUCKET, "pendientes/c-1");

        // Assert
        var captor = ArgumentCaptor.forClass(RemoveObjectArgs.class);
        verify(minioClient).removeObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo(BUCKET);
        assertThat(captor.getValue().object()).isEqualTo("pendientes/c-1");
    }

    @Test
    void debeLanzarMinioOperationException_cuandoFallaElBorrado() throws Exception {
        // Arrange
        org.mockito.Mockito.doThrow(new IOException("caido")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.eliminar(BUCKET, "pendientes/c-1"))
                .isInstanceOf(MinioOperationException.class);
    }
}
