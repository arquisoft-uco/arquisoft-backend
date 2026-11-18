package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionDeEntregableExisteFinderImplTest {

    @Mock
    private EvaluacionOutputPort evaluacionOutputPort;

    @InjectMocks
    private EvaluacionDeEntregableExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(evaluacionOutputPort.existePorEntregable(entregable)).thenReturn(true);

        // Act
        var existe = finder.obtener(entregable);

        // Assert
        assertThat(existe).isTrue();
        verify(evaluacionOutputPort).existePorEntregable(entregable);
    }
}
