package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoEstadoFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoEstadoFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoLaEvaluacionJuradoExiste() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        when(outputPort.obtenerEstadoBloqueado(evaluacionJurado)).thenReturn(Optional.of(
                new EstadoEvaluacionJuradoEntity(evaluacionJurado, jurado, EstadoEvaluacion.FINALIZADA.getId())));

        // Act
        var resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(evaluacionJurado);
        assertThat(resultado.getJurado()).isEqualTo(jurado);
        assertThat(resultado.getEstado()).isEqualTo(EstadoEvaluacion.FINALIZADA);
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionJuradoNoExiste() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        when(outputPort.obtenerEstadoBloqueado(evaluacionJurado)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(evaluacionJurado)).isSameAs(EstadoEvaluacionJuradoDomain.VACIO);
    }
}
