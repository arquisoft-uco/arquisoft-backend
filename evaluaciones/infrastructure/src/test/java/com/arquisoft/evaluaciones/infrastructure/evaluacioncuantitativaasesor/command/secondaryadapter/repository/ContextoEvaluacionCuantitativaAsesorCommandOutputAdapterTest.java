package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.projection.ContextoEvaluacionCuantitativaAsesorProjection;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoEvaluacionCuantitativaAsesorCommandOutputAdapterTest {

    @Mock
    private EvaluacionCuantitativaAsesorCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EvaluacionCuantitativaAsesorCommandOutputAdapter adapter;

    @Test
    void debeMapearElContextoSinLogear_cuandoLaEvaluacionCuantitativaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var proyeccion = mock(ContextoEvaluacionCuantitativaAsesorProjection.class);
        when(proyeccion.getId()).thenReturn(id);
        when(proyeccion.getEvaluacionAsesor()).thenReturn(evaluacionAsesor);
        when(proyeccion.getAsesor()).thenReturn(asesor);
        when(proyeccion.getEstado()).thenReturn("EN_PROGRESO");
        when(repository.buscarContexto(id)).thenReturn(Optional.of(proyeccion));

        // Act
        var resultado = adapter.obtenerContexto(id);

        // Assert
        assertThat(resultado).hasValueSatisfying(contexto -> {
            assertThat(contexto.id()).isEqualTo(id);
            assertThat(contexto.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
            assertThat(contexto.asesor()).isEqualTo(asesor);
            assertThat(contexto.estado()).isEqualTo("EN_PROGRESO");
        });
        verifyNoInteractions(logger);
    }

    @Test
    void debeMapearElContextoPorObservacion_cuandoLaObservacionExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var proyeccion = mock(ContextoEvaluacionCuantitativaAsesorProjection.class);
        when(proyeccion.getId()).thenReturn(id);
        when(proyeccion.getEvaluacionAsesor()).thenReturn(evaluacionAsesor);
        when(proyeccion.getAsesor()).thenReturn(asesor);
        when(proyeccion.getEstado()).thenReturn("EN_PROGRESO");
        when(repository.buscarContextoPorObservacion(observacion)).thenReturn(Optional.of(proyeccion));

        // Act
        var resultado = adapter.obtenerContextoPorObservacion(observacion);

        // Assert
        assertThat(resultado).hasValueSatisfying(contexto -> {
            assertThat(contexto.id()).isEqualTo(id);
            assertThat(contexto.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
            assertThat(contexto.asesor()).isEqualTo(asesor);
            assertThat(contexto.estado()).isEqualTo("EN_PROGRESO");
        });
        verifyNoInteractions(logger);
    }

    @Test
    void debeRetornarVacioPorObservacion_cuandoLaObservacionNoExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        when(repository.buscarContextoPorObservacion(observacion)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerContextoPorObservacion(observacion);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionCuantitativaNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(repository.buscarContexto(id)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerContexto(id);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
