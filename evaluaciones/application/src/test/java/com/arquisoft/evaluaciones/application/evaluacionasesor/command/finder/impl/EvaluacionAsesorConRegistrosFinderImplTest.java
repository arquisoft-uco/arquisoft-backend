package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionAsesorConRegistrosFinderImplTest {

    @Mock
    private EvaluacionAsesorOutputPort outputPort;

    @InjectMocks
    private EvaluacionAsesorConRegistrosFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_conLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.existenRegistrosPorEvaluacion(evaluacion)).thenReturn(true);

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(resultado).isTrue();
    }
}
