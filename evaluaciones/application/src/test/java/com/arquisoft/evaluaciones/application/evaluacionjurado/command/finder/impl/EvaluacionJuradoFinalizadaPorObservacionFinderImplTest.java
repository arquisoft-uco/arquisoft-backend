package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoFinalizadaPorObservacionFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoFinalizadaPorObservacionFinderImpl finder;

    @Test
    void debeDelegarEnEstaFinalizadaPorObservacion_cuandoConsultaElEstadoDeLaEvaluacion() {
        // Arrange
        var observacion = UUID.randomUUID();
        when(outputPort.estaFinalizadaPorObservacion(observacion)).thenReturn(true);

        // Act
        var resultado = finder.obtener(observacion);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort, times(1)).estaFinalizadaPorObservacion(observacion);
    }
}
