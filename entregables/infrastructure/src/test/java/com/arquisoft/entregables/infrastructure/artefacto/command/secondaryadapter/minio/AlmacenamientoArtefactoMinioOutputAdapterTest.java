package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.minio;

import com.arquisoft.entregables.infrastructure.config.EntregablesAlmacenamientoProperties;
import com.arquisoft.entregables.infrastructure.transaccion.SincronizacionTransaccional;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlmacenamientoArtefactoMinioOutputAdapterTest {

    private static final String BUCKET = "bucket-entregables";
    private static final String BUCKET_ARTEFACTOS = "bucket-artefactos";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;

    @Mock
    private AppLogger logger;

    private AlmacenamientoArtefactoMinioOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        var propiedades = new EntregablesAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        propiedades.setBucketArtefactos(BUCKET_ARTEFACTOS);
        adapter = new AlmacenamientoArtefactoMinioOutputAdapter(
                almacenamientoObjetos, propiedades, new SincronizacionTransaccional(logger), logger);
    }

    @Test
    void debeDevolverClaveYEtag_cuandoElObjetoOrigenExiste() {
        // Arrange
        when(almacenamientoObjetos.consultarMetadatos(BUCKET_ARTEFACTOS, "pendientes/a.pdf"))
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
        when(almacenamientoObjetos.consultarMetadatos(BUCKET_ARTEFACTOS, "pendientes/a.pdf"))
                .thenReturn(MetadatosObjeto.VACIO);

        // Act
        var origen = adapter.obtenerMetadatosOrigen("pendientes/a.pdf");

        // Assert
        assertThat(origen).isEmpty();
    }

    @Test
    void debeCopiarEntreBucketsCondicionadoAlEtag_cuandoSeCopia() {
        // Act
        adapter.copiar("pendientes/a.pdf", "artefactos/x/v1", "etag-7");

        // Assert
        verify(almacenamientoObjetos)
                .copiar(BUCKET_ARTEFACTOS, "pendientes/a.pdf", BUCKET, "artefactos/x/v1", "etag-7");
    }

    @AfterEach
    void limpiarSincronizacion() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void debeBorrarLaCopiaNueva_cuandoLaTransaccionRevierteDespuesDeCopiar() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();

        // Act
        adapter.copiar("pendientes/a.pdf", "artefactos/x/v2", "etag-7");
        verify(almacenamientoObjetos, never()).eliminar(BUCKET, "artefactos/x/v2");
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        // Assert
        verify(almacenamientoObjetos).eliminar(BUCKET, "artefactos/x/v2");
        verify(almacenamientoObjetos, never()).eliminar(BUCKET, "artefactos/x/v1");
    }

    @Test
    void noDebeBorrarLaCopiaNueva_cuandoLaTransaccionConfirma() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();

        // Act
        adapter.copiar("pendientes/a.pdf", "artefactos/x/v2", "etag-7");
        TransactionSynchronizationUtils.triggerAfterCommit();
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_COMMITTED);

        // Assert
        verify(almacenamientoObjetos, never()).eliminar(BUCKET, "artefactos/x/v2");
    }

    @Test
    void debeBorrarLaCopiaAnteriorSoloTrasConfirmar_cuandoHayTransaccionActiva() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();

        // Act
        adapter.eliminarTrasConfirmar("artefactos/x/v1");
        verify(almacenamientoObjetos, never()).eliminar(BUCKET, "artefactos/x/v1");
        TransactionSynchronizationUtils.triggerAfterCommit();

        // Assert
        verify(almacenamientoObjetos).eliminar(BUCKET, "artefactos/x/v1");
    }

    @Test
    void noDebeBorrarLaCopiaAnterior_cuandoLaTransaccionRevierte() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();

        // Act
        adapter.eliminarTrasConfirmar("artefactos/x/v1");
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        // Assert
        verify(almacenamientoObjetos, never()).eliminar(BUCKET, "artefactos/x/v1");
    }

    @Test
    void debeEliminarEnElBucketPropio_cuandoSeBorraLaCopia() {
        // Act
        adapter.eliminar("artefactos/x/v1");

        // Assert
        verify(almacenamientoObjetos).eliminar(BUCKET, "artefactos/x/v1");
    }
}
