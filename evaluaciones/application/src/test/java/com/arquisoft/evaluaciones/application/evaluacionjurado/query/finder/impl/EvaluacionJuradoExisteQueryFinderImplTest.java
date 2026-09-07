package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
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
class EvaluacionJuradoExisteQueryFinderImplTest {

    @Mock
    private EvaluacionJuradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoExisteQueryFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_yRetornarLoQueEsteResponda() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        when(outputPort.existePorId(evaluacionJurado)).thenReturn(true);

        // Act
        Boolean resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorId(evaluacionJurado);
    }

    @Test
    void debeRetornarFalse_cuandoElOutputPortNoEncuentraLaEvaluacion() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        when(outputPort.existePorId(evaluacionJurado)).thenReturn(false);

        // Act
        Boolean resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado).isFalse();
    }
}
