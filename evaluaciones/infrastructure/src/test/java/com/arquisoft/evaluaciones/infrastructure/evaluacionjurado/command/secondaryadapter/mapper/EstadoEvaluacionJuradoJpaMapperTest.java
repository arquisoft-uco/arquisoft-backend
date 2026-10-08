package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.EstadoEvaluacionJuradoProjection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstadoEvaluacionJuradoJpaMapperTest {

    @Test
    void debeMapearLaProyeccionAEntity() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        var proyeccion = mock(EstadoEvaluacionJuradoProjection.class);
        when(proyeccion.getId()).thenReturn(evaluacionJurado);
        when(proyeccion.getJurado()).thenReturn(jurado);
        when(proyeccion.getEstado()).thenReturn("EN_PROGRESO");

        // Act
        var entity = EstadoEvaluacionJuradoJpaMapper.toEntity(proyeccion);

        // Assert
        assertThat(entity.id()).isEqualTo(evaluacionJurado);
        assertThat(entity.jurado()).isEqualTo(jurado);
        assertThat(entity.estado()).isEqualTo("EN_PROGRESO");
    }
}
