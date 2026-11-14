package com.arquisoft.shared.minio;

import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.minio.config.MinioClientFirmante;
import com.arquisoft.shared.minio.config.MinioProperties;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
import io.minio.CopyObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.PutObjectArgs;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
        almacenamiento = new MinioAlmacenamientoObjetos(minioClient, new MinioClientFirmante(minioClient), propiedades);
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
    void debeCopiarEntreBucketsDistintos_cuandoSeIndicanOrigenYDestino() throws Exception {
        // Act
        almacenamiento.copiar("artefactos", "pendientes/c-1", "entregables", "artefactos/a-1/v1", "\"abc123\"");

        // Assert
        var captor = ArgumentCaptor.forClass(CopyObjectArgs.class);
        verify(minioClient).copyObject(captor.capture());
        assertThat(captor.getValue().source().bucket()).isEqualTo("artefactos");
        assertThat(captor.getValue().source().object()).isEqualTo("pendientes/c-1");
        assertThat(captor.getValue().source().matchETag()).isEqualTo("abc123");
        assertThat(captor.getValue().bucket()).isEqualTo("entregables");
        assertThat(captor.getValue().object()).isEqualTo("artefactos/a-1/v1");
    }

    @Test
    void debeDelegarEnElDeCincoParametrosConElMismoBucket_cuandoSeUsaLaSobrecargaDeCuatro() {
        // Arrange
        var delegante = mock(AlmacenamientoObjetos.class, CALLS_REAL_METHODS);
        doNothing().when(delegante).copiar(any(), any(), any(), any(), any());

        // Act
        delegante.copiar(BUCKET, "origen", "destino", "etag");

        // Assert
        verify(delegante).copiar(BUCKET, "origen", BUCKET, "destino", "etag");
    }

    @Test
    void debeCopiarSinReemplazarMetadatos_cuandoCopia() throws Exception {
        // Act
        almacenamiento.copiar(BUCKET, "pendientes/c-1", "versiones/v-1", "abc");

        // Assert
        var captor = ArgumentCaptor.forClass(CopyObjectArgs.class);
        verify(minioClient).copyObject(captor.capture());
        assertThat(captor.getValue().metadataDirective()).isNull();
        assertThat(captor.getValue().headers().containsKey("x-amz-metadata-directive")).isFalse();
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

    @Test
    void debeSubirObjetoConTamanoYTipo_cuandoMinioResponde() throws Exception {
        // Arrange
        var contenido = new ByteArrayInputStream("zip".getBytes(StandardCharsets.UTF_8));

        // Act
        almacenamiento.subir(BUCKET, "paquetes/e-1.zip", contenido, 3L, "application/zip");

        // Assert
        var captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo(BUCKET);
        assertThat(captor.getValue().object()).isEqualTo("paquetes/e-1.zip");
        assertThat(captor.getValue().objectSize()).isEqualTo(3L);
        assertThat(captor.getValue().contentType()).isEqualTo("application/zip");
    }

    @Test
    void debeLanzarSubidaFallida_cuandoMinioFalla() throws Exception {
        // Arrange
        org.mockito.Mockito.doThrow(new IOException("caido")).when(minioClient).putObject(any(PutObjectArgs.class));
        var contenido = new ByteArrayInputStream(new byte[] {1});

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.subir(BUCKET, "paquetes/e-1.zip", contenido, 1L, "application/zip"))
                .isInstanceOfSatisfying(MinioOperationException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(AppCodes.Minio.SUBIDA_FALLIDA));
    }

    private GetPresignedObjectUrlArgs firmar(String nombreArchivo) throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("https://s3.arquisoft.test/firmada");
        almacenamiento.generarEnlaceDescarga(new SolicitudEnlaceDescarga(BUCKET, "versiones/v-1", nombreArchivo));
        var captor = ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(minioClient).getPresignedObjectUrl(captor.capture());
        return captor.getValue();
    }

    private static String contentDisposition(GetPresignedObjectUrlArgs args) {
        return args.extraQueryParams().get("response-content-disposition").iterator().next();
    }

    @Test
    void debeGenerarUrlDeDescarga_conGetBucketKeyYExpiracion() throws Exception {
        // Arrange
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("https://s3.arquisoft.test/firmada");

        // Act
        var enlace = almacenamiento.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(BUCKET, "versiones/v-1", "formato.pdf"));

        // Assert
        var captor = ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(minioClient).getPresignedObjectUrl(captor.capture());
        assertThat(captor.getValue().method()).isEqualTo(Method.GET);
        assertThat(captor.getValue().bucket()).isEqualTo(BUCKET);
        assertThat(captor.getValue().object()).isEqualTo("versiones/v-1");
        assertThat(captor.getValue().expiry()).isEqualTo(5 * 60);
        assertThat(enlace.url()).isEqualTo("https://s3.arquisoft.test/firmada");
    }

    @Test
    void debeCalcularExpiraEn_conLosMinutosConfigurados() throws Exception {
        // Arrange
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("https://x");
        var antes = Instant.now();

        // Act
        var enlace = almacenamiento.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(BUCKET, "versiones/v-1", "formato.pdf"));

        // Assert
        var despues = Instant.now();
        assertThat(enlace.expiraEn())
                .isBetween(antes.plus(5, ChronoUnit.MINUTES), despues.plus(5, ChronoUnit.MINUTES));
    }

    @Test
    void debeIncluirContentDispositionAttachment_conNombreOriginal() throws Exception {
        // Act
        var args = firmar("formato final.pdf");

        // Assert
        assertThat(contentDisposition(args))
                .isEqualTo("attachment; filename=\"formato final.pdf\"; filename*=UTF-8''formato%20final.pdf");
    }

    @Test
    void debeQuitarComillasSaltosYPuntoYComa_delNombreDeArchivo() throws Exception {
        // Act
        var args = firmar("for\"ma\r\nto;.pdf");

        // Assert
        assertThat(contentDisposition(args))
                .isEqualTo("attachment; filename=\"formato.pdf\"; filename*=UTF-8''formato.pdf");
    }

    @Test
    void debeUsarFallbackAscii_cuandoElNombreTraeCaracteresEspeciales() throws Exception {
        // Act
        var args = firmar("Guía\\final.pdf");

        // Assert
        assertThat(contentDisposition(args))
                .isEqualTo("attachment; filename=\"Gu_a_final.pdf\"; filename*=UTF-8''Gu%C3%ADa%5Cfinal.pdf");
    }

    @Test
    void debeLanzarEnlaceDescargaFallido_cuandoElClienteFalla() throws Exception {
        // Arrange
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new IOException("caido"));

        // Act & Assert
        assertThatThrownBy(() -> almacenamiento.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(BUCKET, "versiones/v-1", "formato.pdf")))
                .isInstanceOfSatisfying(MinioOperationException.class, excepcion ->
                        assertThat(excepcion.getCodigoError()).isEqualTo(AppCodes.Minio.URL_DESCARGA_FALLIDA));
    }
}
