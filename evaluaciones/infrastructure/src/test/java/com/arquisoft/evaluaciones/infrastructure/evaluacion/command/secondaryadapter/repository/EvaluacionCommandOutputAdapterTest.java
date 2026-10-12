package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.ContextoAsignacionEvaluacionProjection;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCommandOutputAdapterTest {

    @Mock
    private EvaluacionCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EvaluacionCommandOutputAdapter adapter;

    @Test
    void debeDelegarActualizacionYLoguear_cuandoSeActualizaElEstado() {
        // Arrange
        var evaluacion = UUID.randomUUID();

        // Act
        adapter.actualizarEstado(evaluacion, "EN_PROGRESO");

        // Assert
        verify(repository).actualizarEstado(evaluacion, "EN_PROGRESO");
        verify(logger).debug(EvaluacionKey.LOG_ESTADO_ACTUALIZADO, evaluacion, "EN_PROGRESO");
    }

    @Test
    void debeMapearLaProyeccionAEntity_cuandoSeObtieneElContextoDeAsignacion() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var proyeccion = mock(ContextoAsignacionEvaluacionProjection.class);
        when(proyeccion.getEvaluacion()).thenReturn(evaluacion);
        when(proyeccion.getEstado()).thenReturn("EN_PROGRESO");
        when(repository.buscarContextoAsignacionBloqueado(evaluacion)).thenReturn(Optional.of(proyeccion));

        // Act
        var contexto = adapter.obtenerContextoAsignacionBloqueado(evaluacion);

        // Assert
        assertThat(contexto).hasValueSatisfying(entity -> {
            assertThat(entity.evaluacion()).isEqualTo(evaluacion);
            assertThat(entity.estado()).isEqualTo("EN_PROGRESO");
        });
    }

    @Test
    void debeRetornarVacio_cuandoElContextoDeAsignacionNoExiste() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        when(repository.buscarContextoAsignacionBloqueado(evaluacion)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(adapter.obtenerContextoAsignacionBloqueado(evaluacion)).isEmpty();
    }
}
