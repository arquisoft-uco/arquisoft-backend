package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.ContextoRegistroEvaluacionJuradoProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoRegistroEvaluacionJuradoCommandOutputAdapterTest {

    @Mock
    private EvaluacionJuradoCommandRepository repository;

    @InjectMocks
    private ContextoRegistroEvaluacionJuradoCommandOutputAdapter adapter;

    @Test
    void debeMapearElContexto_cuandoLaEvaluacionDeJuradoExiste() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        var proyeccion = mock(ContextoRegistroEvaluacionJuradoProjection.class);
        when(proyeccion.getId()).thenReturn(evaluacionJurado);
        when(proyeccion.getEvaluacion()).thenReturn(evaluacion);
        when(proyeccion.getEstado()).thenReturn("PENDIENTE");
        when(proyeccion.getEntregable()).thenReturn(entregable);
        when(repository.buscarContextoBloqueado(evaluacionJurado)).thenReturn(Optional.of(proyeccion));

        // Act
        var resultado = adapter.obtenerContextoBloqueado(evaluacionJurado);

        // Assert
        assertThat(resultado).hasValueSatisfying(contexto -> {
            assertThat(contexto.id()).isEqualTo(evaluacionJurado);
            assertThat(contexto.evaluacion()).isEqualTo(evaluacion);
            assertThat(contexto.estado()).isEqualTo("PENDIENTE");
            assertThat(contexto.entregable()).isEqualTo(entregable);
        });
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionDeJuradoNoExiste() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        when(repository.buscarContextoBloqueado(evaluacionJurado)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerContextoBloqueado(evaluacionJurado);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
