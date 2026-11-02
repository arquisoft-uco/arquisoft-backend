package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.minio;

import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosAlmacenamientoProperties;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.exception.ArchivoVersionNoDisponibleException;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.EnlaceDescarga;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnlaceDescargaVersionMinioQueryOutputAdapterTest {

    private static final String BUCKET = "repositorio-artefactos";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;

    private EnlaceDescargaVersionMinioQueryOutputAdapter adaptador;

    @BeforeEach
    void setUp() {
        var propiedades = new RepositorioArtefactosAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        propiedades.setTamanoMaximoBytes(5_242_880L);
        adaptador = new EnlaceDescargaVersionMinioQueryOutputAdapter(almacenamientoObjetos, propiedades);
    }

    @Test
    void debePedirEnlaceConBucketDelContextoYUriRelativa_yDevolverUrlYExpiracion_cuandoElObjetoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var key = "versiones/" + version;
        var expiraEn = Instant.parse("2026-11-02T10:05:00Z");
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, key))
                .thenReturn(new MetadatosObjeto(2048L, "application/pdf", "etag", Map.of()));
        when(almacenamientoObjetos.generarEnlaceDescarga(any(SolicitudEnlaceDescarga.class)))
                .thenReturn(new EnlaceDescarga("https://s3.test/firmada", expiraEn));

        // Act
        var resultado = adaptador.generar(version, key, "formato.pdf");

        // Assert
        var captor = ArgumentCaptor.forClass(SolicitudEnlaceDescarga.class);
        verify(almacenamientoObjetos).generarEnlaceDescarga(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new SolicitudEnlaceDescarga(BUCKET, key, "formato.pdf"));
        assertThat(resultado.url()).isEqualTo("https://s3.test/firmada");
        assertThat(resultado.expiraEn()).isEqualTo(expiraEn);
    }

    @Test
    void debeLanzarArchivoNoDisponible_cuandoElObjetoNoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var key = "versiones/" + version;
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, key)).thenReturn(MetadatosObjeto.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> adaptador.generar(version, key, "formato.pdf"))
                .isInstanceOfSatisfying(ArchivoVersionNoDisponibleException.class, excepcion -> {
                    assertThat(excepcion.getError().getMessage())
                            .doesNotContain(version.toString())
                            .doesNotContain(key);
                    assertThat(excepcion.getError().getDetalle())
                            .contains(version.toString())
                            .contains(key);
                });
        verify(almacenamientoObjetos, never()).generarEnlaceDescarga(any());
    }

    @Test
    void debePropagarMinioOperationException_cuandoMinioNoResponde() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var key = "versiones/" + version;
        var fallo = MinioOperationException.enlaceDescargaFallido(new RuntimeException("caido"));
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, key)).thenThrow(fallo);

        // Act & Assert
        assertThatThrownBy(() -> adaptador.generar(version, key, "formato.pdf")).isSameAs(fallo);
        verify(almacenamientoObjetos, never()).generarEnlaceDescarga(any());
    }
}
