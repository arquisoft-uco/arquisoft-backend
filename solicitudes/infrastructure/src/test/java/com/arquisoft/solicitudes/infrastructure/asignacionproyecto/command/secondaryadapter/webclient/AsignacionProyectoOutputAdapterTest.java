package com.arquisoft.solicitudes.infrastructure.asignacionproyecto.command.secondaryadapter.webclient;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AsignacionProyectoOutputAdapterTest {

    private final AppLogger logger = mock(AppLogger.class);
    private final AsignacionProyectoOutputAdapter adapter = new AsignacionProyectoOutputAdapter(logger);

    @Test
    void debeAprobarSiempreYRegistrarWarn_mientrasProyectosNoEsteDisponible() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act
        boolean resultado = adapter.esResponsableAsignado(estudiante, coordinador);

        // Assert
        assertThat(resultado).isTrue();
        verify(logger).warn(any(ClaveMensaje.class), any(), any());
    }
}
