package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.minio;

import com.arquisoft.entregables.infrastructure.config.EntregablesAlmacenamientoProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlmacenamientoArtefactoMinioOutputAdapterTest {

    private static final String BUCKET = "bucket-entregables";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;

    @Mock
    private AppLogger logger;

    private AlmacenamientoArtefactoMinioOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        var propiedades = new EntregablesAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        adapter = new AlmacenamientoArtefactoMinioOutputAdapter(almacenamientoObjetos, propiedades, logger);
    }

    @Test
    void debeDevolverClaveYEtag_cuandoElObjetoOrigenExiste() {
        // Arrange
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, "pendientes/a.pdf"))
                .thenReturn(new MetadatosObjeto(10L, "application/pdf", "etag-7", Map.of()));

        // Act
        var origen = adapter.obtenerMetadatosOrigen("pendientes/a.pdf");

        // Assert
        assertThat(origen).isPresent();
        assertThat(origen.get().clave()).isEqualTo("pendientes/a.pdf");
        assertThat(origen.get().etag()).isEqualTo("etag-7");
    }

    @Test
    void debeDevolverVacio_cuandoElObjetoOrigenNoExiste() {
        // Arrange
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, "pendientes/a.pdf"))
                .thenReturn(MetadatosObjeto.VACIO);

        // Act
        var origen = adapter.obtenerMetadatosOrigen("pendientes/a.pdf");

        // Assert
        assertThat(origen).isEmpty();
    }

    @Test
    void debeCopiarCondicionadoAlEtagEnElBucketPropio_cuandoSeCopia() {
        // Act
        adapter.copiar("pendientes/a.pdf", "entregables/artefactos/x/v1", "etag-7");

        // Assert
        verify(almacenamientoObjetos).copiar(BUCKET, "pendientes/a.pdf", "entregables/artefactos/x/v1", "etag-7");
    }

    @Test
    void debeEliminarEnElBucketPropio_cuandoSeBorraLaCopia() {
        // Act
        adapter.eliminar("entregables/artefactos/x/v1");

        // Assert
        verify(almacenamientoObjetos).eliminar(BUCKET, "entregables/artefactos/x/v1");
    }
}
