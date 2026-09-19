package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoFinalizadaFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoFinalizadaFinderImpl finder;

    @Test
    void debeRetornarTrue_cuandoElPuertoIndicaQueLaEvaluacionEstaFinalizada() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        when(outputPort.estaFinalizada(evaluacionJurado)).thenReturn(true);

        // Act
        Boolean resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).estaFinalizada(evaluacionJurado);
    }

    @Test
    void debeRetornarFalse_cuandoElPuertoIndicaQueLaEvaluacionNoEstaFinalizada() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        when(outputPort.estaFinalizada(evaluacionJurado)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(evaluacionJurado)).isFalse();
    }
}
