package com.arquisoft.evaluaciones.infrastructure.estudiantesproyectogrado.query.secondaryadapter.webclient;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EstudiantesProyectoGradoOutputAdapterTest {

    private final AppLogger logger = mock(AppLogger.class);
    private final EstudiantesProyectoGradoOutputAdapter adapter = new EstudiantesProyectoGradoOutputAdapter(logger);

    @Test
    void debeRetornarElRosterFijoYRegistrarWarn_mientrasProyectosNoEsteDisponible() {
        // Act
        var resultado = adapter.obtenerEstudiantes("Proyecto");

        // Assert
        assertThat(resultado).isNotEmpty();
        verify(logger).warn(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarSiempreElMismoRoster_sinImportarElProyecto() {
        // Act
        var resultado1 = adapter.obtenerEstudiantes("Proyecto Uno");
        var resultado2 = adapter.obtenerEstudiantes("Proyecto Dos");

        // Assert
        assertThat(resultado1).isEqualTo(resultado2);
    }
}
