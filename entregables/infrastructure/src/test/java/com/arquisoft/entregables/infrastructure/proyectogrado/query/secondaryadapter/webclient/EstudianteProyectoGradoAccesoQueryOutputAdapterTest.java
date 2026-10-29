package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.webclient;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EstudianteProyectoGradoAccesoQueryOutputAdapterTest {

    @Test
    void debeRetornarTrueYLoguearWarn_porSerStub() {
        // Arrange
        var logger = mock(AppLogger.class);
        var adapter = new EstudianteProyectoGradoAccesoQueryOutputAdapter(logger);
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var pertenece = adapter.perteneceAlProyecto(estudiante, proyecto);

        // Assert
        assertThat(pertenece).isTrue();
        verify(logger).warn(eq(ProyectoGradoKey.LOG_PERTENENCIA_SIMULADA), eq(estudiante), eq(proyecto));
    }
}
