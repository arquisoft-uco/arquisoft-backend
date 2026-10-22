package com.arquisoft.mapas_ruta.infrastructure.asignacionasesor.command.secondaryadapter.webclient;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AsignacionAsesorOutputAdapterTest {

    @Test
    void debeDevolverTrueYLoguearWarn() {
        // Arrange
        var logger = mock(AppLogger.class);
        var adapter = new AsignacionAsesorOutputAdapter(logger);
        var asesor = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var asignado = adapter.estaAsignado(proyectoGrado, asesor);

        // Assert
        assertThat(asignado).isTrue();
        verify(logger).warn(any(ClaveMensaje.class), eq(asesor), eq(proyectoGrado));
    }
}
