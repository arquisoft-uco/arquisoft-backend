package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
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
class EvaluacionCuantitativaJuradoPorItemExisteFinderImplTest {

    @Mock
    private EvaluacionCuantitativaJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionCuantitativaJuradoPorItemExisteFinderImpl finder;

    @Test
    void debeRetornarTrue_cuandoPuertoReportaUso() {
        // Arrange
        var item = UUID.randomUUID();
        when(outputPort.existePorItem(item)).thenReturn(true);

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorItem(item);
    }

    @Test
    void debeRetornarFalse_cuandoPuertoNoReportaUso() {
        // Arrange
        var item = UUID.randomUUID();
        when(outputPort.existePorItem(item)).thenReturn(false);

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isFalse();
        verify(outputPort).existePorItem(item);
    }
}
