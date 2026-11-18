package com.arquisoft.entregables.infrastructure.transaccion;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SincronizacionTransaccionalTest {

    private final AppLogger logger = mock(AppLogger.class);
    private final SincronizacionTransaccional sincronizacion = new SincronizacionTransaccional(logger);

    @AfterEach
    void limpiarSincronizacion() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void debeEjecutarTrasConfirmarSoloEnElCommit_cuandoHayTransaccionActiva() {
        // Arrange
        var ejecuciones = new ArrayList<String>();
        TransactionSynchronizationManager.initSynchronization();
        sincronizacion.trasConfirmar(() -> ejecuciones.add("borrado"), "bucket", "clave");

        // Act
        var antes = new ArrayList<>(ejecuciones);
        TransactionSynchronizationUtils.triggerAfterCommit();

        // Assert
        assertThat(antes).isEmpty();
        assertThat(ejecuciones).containsExactly("borrado");
    }

    @Test
    void noDebeEjecutarTrasConfirmar_cuandoLaTransaccionRevierte() {
        // Arrange
        var ejecuciones = new ArrayList<String>();
        TransactionSynchronizationManager.initSynchronization();
        sincronizacion.trasConfirmar(() -> ejecuciones.add("borrado"), "bucket", "clave");

        // Act
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        // Assert
        assertThat(ejecuciones).isEmpty();
    }

    @Test
    void debeEjecutarTrasReversionSoloEnRollback_cuandoHayTransaccionActiva() {
        // Arrange
        var ejecuciones = new ArrayList<String>();
        TransactionSynchronizationManager.initSynchronization();
        sincronizacion.trasReversion(() -> ejecuciones.add("compensacion"), "bucket", "clave");

        // Act
        TransactionSynchronizationUtils.triggerAfterCommit();
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_COMMITTED);
        var trasCommit = new ArrayList<>(ejecuciones);
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        // Assert
        assertThat(trasCommit).isEmpty();
        assertThat(ejecuciones).containsExactly("compensacion");
    }

    @Test
    void debeEjecutarTrasConfirmarDeInmediatoYNoTrasReversion_cuandoNoHayTransaccion() {
        // Arrange
        var ejecuciones = new ArrayList<String>();

        // Act
        sincronizacion.trasConfirmar(() -> ejecuciones.add("borrado"), "bucket", "clave");
        sincronizacion.trasReversion(() -> ejecuciones.add("compensacion"), "bucket", "clave");

        // Assert
        assertThat(ejecuciones).containsExactly("borrado");
    }

    @Test
    void noDebePropagarYDebeRegistrarErrorConLaCausa_cuandoElEfectoLanza() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();
        sincronizacion.trasConfirmar(() -> {
            throw new IllegalStateException("minio caido");
        }, "bucket", "clave");
        sincronizacion.trasReversion(() -> {
            throw new IllegalStateException("minio caido");
        }, "bucket", "otra-clave");

        // Act & Assert
        assertThatCode(() -> {
            TransactionSynchronizationUtils.triggerAfterCommit();
            TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        }).doesNotThrowAnyException();
        verify(logger).error(any(ClaveMensaje.class), any(IllegalStateException.class), eq("bucket"), eq("clave"));
        verify(logger).error(any(ClaveMensaje.class), any(IllegalStateException.class), eq("bucket"),
                eq("otra-clave"));
    }
}
