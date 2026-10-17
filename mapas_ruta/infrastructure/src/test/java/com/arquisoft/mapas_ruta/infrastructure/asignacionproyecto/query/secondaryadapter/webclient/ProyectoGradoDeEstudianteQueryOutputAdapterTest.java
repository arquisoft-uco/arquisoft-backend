package com.arquisoft.mapas_ruta.infrastructure.asignacionproyecto.query.secondaryadapter.webclient;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.AsignacionProyectoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProyectoGradoDeEstudianteQueryOutputAdapterTest {

    private final AppLogger logger = mock(AppLogger.class);
    private final ProyectoGradoDeEstudianteQueryOutputAdapter adapter =
            new ProyectoGradoDeEstudianteQueryOutputAdapter(logger);

    @Test
    void debeRetornarElMismoProyecto_cuandoSeConsultanEstudiantesDistintos() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();

        // Act
        var proyectoDelPrimero = adapter.obtenerProyectoGrado(primero);
        var proyectoDelSegundo = adapter.obtenerProyectoGrado(segundo);

        // Assert
        assertThat(proyectoDelPrimero).isPresent().isEqualTo(proyectoDelSegundo);
    }

    @Test
    void debeRegistrarWarnConEstudianteYProyecto_cuandoSimulaElProyecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var resultado = adapter.obtenerProyectoGrado(estudiante);

        // Assert
        var proyectoGrado = resultado.orElseThrow();
        verify(logger).warn(eq(AsignacionProyectoKey.LOG_PROYECTO_ESTUDIANTE_SIMULADO),
                eq(estudiante), eq(proyectoGrado));
    }
}
