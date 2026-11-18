package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
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
class EvaluacionCuantitativaAsesorPorItemExisteFinderImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @InjectMocks
    private EvaluacionCuantitativaAsesorPorItemExisteFinderImpl finder;

    @Test
    void debeRetornarResultadoDelPuerto_cuandoConsultaExistenciaPorItem() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        when(outputPort.existePorItem(item)).thenReturn(true);

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorItem(item);
    }
}
