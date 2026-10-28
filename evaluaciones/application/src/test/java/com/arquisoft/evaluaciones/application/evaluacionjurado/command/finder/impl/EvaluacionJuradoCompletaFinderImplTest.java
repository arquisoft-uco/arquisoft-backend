package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoCompletaFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoCompletaFinderImpl finder;

    @Test
    void debeDevolverLaCompletitudDelPuerto() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        when(outputPort.estaCompleta(evaluacionJurado)).thenReturn(true);

        // Act
        var completa = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(completa).isTrue();
    }
}
