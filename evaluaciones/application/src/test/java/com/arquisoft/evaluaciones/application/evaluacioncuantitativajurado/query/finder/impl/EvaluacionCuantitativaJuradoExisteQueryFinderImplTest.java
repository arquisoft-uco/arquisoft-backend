package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCuantitativaJuradoExisteQueryFinderImplTest {

    @Mock
    private EvaluacionCuantitativaJuradoQueryOutputPort outputPort;

    @InjectMocks
    private EvaluacionCuantitativaJuradoExisteQueryFinderImpl finder;

    @Test
    void debeDelegarEnExistePorId_yRetornarLoQueDiceElPuerto() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        when(outputPort.existePorId(evaluacionCuantitativaJurado)).thenReturn(true);

        // Act
        var existe = finder.obtener(evaluacionCuantitativaJurado);

        // Assert
        assertThat(existe).isTrue();
    }
}
