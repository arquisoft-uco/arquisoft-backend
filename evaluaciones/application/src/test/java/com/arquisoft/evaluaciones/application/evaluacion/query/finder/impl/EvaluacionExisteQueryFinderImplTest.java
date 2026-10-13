package com.arquisoft.evaluaciones.application.evaluacion.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionAccesoQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionExisteQueryFinderImplTest {

    @Mock
    private EvaluacionAccesoQueryOutputPort evaluacionAccesoQueryOutputPort;

    private EvaluacionExisteQueryFinderImpl finder;

    @Test
    void debeRetornarTrue_cuandoElPuertoConfirmaExistencia() {
        // Arrange
        finder = new EvaluacionExisteQueryFinderImpl(evaluacionAccesoQueryOutputPort);
        var evaluacion = UUID.randomUUID();
        when(evaluacionAccesoQueryOutputPort.existePorId(evaluacion)).thenReturn(true);

        // Act
        var existe = finder.obtener(evaluacion);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElPuertoNiegaExistencia() {
        // Arrange
        finder = new EvaluacionExisteQueryFinderImpl(evaluacionAccesoQueryOutputPort);
        var evaluacion = UUID.randomUUID();
        when(evaluacionAccesoQueryOutputPort.existePorId(evaluacion)).thenReturn(false);

        // Act
        var existe = finder.obtener(evaluacion);

        // Assert
        assertThat(existe).isFalse();
    }
}
