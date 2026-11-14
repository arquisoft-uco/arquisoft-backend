package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.transaccion;

import com.arquisoft.entregables.infrastructure.transaccion.SincronizacionTransaccional;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class EjecutorTransaccionalOutputAdapterTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void debeAbrirTransaccionSoloLecturaYDevolverElValor_cuandoEjecutaEnLectura() {
        // Arrange
        var ejecutor = new EjecutorTransaccionalOutputAdapter(transactionManager);

        // Act
        var resultado = ejecutor.enLectura(() -> new boolean[] {
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.isCurrentTransactionReadOnly()});

        // Assert
        assertThat(resultado).containsExactly(true, true);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
    }

    @Test
    void debeConfirmarCadaEscrituraPorSeparado_cuandoSeEjecutanDosEnSecuencia() {
        // Arrange
        var ejecutor = new EjecutorTransaccionalOutputAdapter(transactionManager);
        var sincronizacion = new SincronizacionTransaccional(mock(AppLogger.class));
        var eventos = new ArrayList<String>();

        // Act
        var primero = ejecutor.enEscritura(() -> {
            eventos.add("inicio-1");
            sincronizacion.trasConfirmar(() -> eventos.add("commit-1"), "bucket", "clave-1");
            return TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        });
        var segundo = ejecutor.enEscritura(() -> {
            eventos.add("inicio-2");
            return TransactionSynchronizationManager.isActualTransactionActive();
        });

        // Assert
        assertThat(primero).isFalse();
        assertThat(segundo).isTrue();
        assertThat(eventos).containsExactly("inicio-1", "commit-1", "inicio-2");
    }

    @Test
    void debeRevertirYEjecutarLaCompensacion_cuandoLaEscrituraLanza() {
        // Arrange
        var ejecutor = new EjecutorTransaccionalOutputAdapter(transactionManager);
        var sincronizacion = new SincronizacionTransaccional(mock(AppLogger.class));
        var eventos = new ArrayList<String>();

        // Act & Assert
        assertThatThrownBy(() -> ejecutor.enEscritura(() -> {
            sincronizacion.trasReversion(() -> eventos.add("compensacion"), "bucket", "clave");
            sincronizacion.trasConfirmar(() -> eventos.add("commit"), "bucket", "clave");
            throw new IllegalStateException("falla");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(eventos).containsExactly("compensacion");
    }
}
