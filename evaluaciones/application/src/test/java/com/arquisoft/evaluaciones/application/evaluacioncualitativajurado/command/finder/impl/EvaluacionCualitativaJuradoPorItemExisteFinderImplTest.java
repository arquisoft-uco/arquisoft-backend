package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCualitativaJuradoPorItemExisteFinderImplTest {

    @Mock
    private EvaluacionCualitativaJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionCualitativaJuradoPorItemExisteFinderImpl finder;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void debeDelegarEnOutputPort_cuandoConsultaPorItem(boolean enUso) {
        // Arrange
        var item = UUID.randomUUID();
        when(outputPort.existePorItem(item)).thenReturn(enUso);

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isEqualTo(enUso);
        verify(outputPort).existePorItem(item);
    }
}
