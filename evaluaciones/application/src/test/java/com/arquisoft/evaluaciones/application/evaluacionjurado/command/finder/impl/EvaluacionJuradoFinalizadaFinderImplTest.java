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
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        when(outputPort.estaFinalizada(evaluacionCuantitativaJurado)).thenReturn(true);

        // Act
        Boolean resultado = finder.obtener(evaluacionCuantitativaJurado);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).estaFinalizada(evaluacionCuantitativaJurado);
    }

    @Test
    void debeRetornarFalse_cuandoElPuertoIndicaQueLaEvaluacionNoEstaFinalizada() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        when(outputPort.estaFinalizada(evaluacionCuantitativaJurado)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(evaluacionCuantitativaJurado)).isFalse();
    }
}
