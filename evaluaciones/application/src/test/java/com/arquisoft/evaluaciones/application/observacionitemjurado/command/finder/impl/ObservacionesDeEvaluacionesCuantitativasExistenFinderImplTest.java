package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionesDeEvaluacionesCuantitativasExistenFinderImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @InjectMocks
    private ObservacionesDeEvaluacionesCuantitativasExistenFinderImpl finder;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void debeDelegarEnPuertoYDevolverSuValor_cuandoConsultaLasObservacionesDelLote(boolean existen) {
        // Arrange
        var evaluaciones = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(outputPort.existenPorEvaluacionesCuantitativas(evaluaciones)).thenReturn(existen);

        // Act
        var resultado = finder.obtener(evaluaciones);

        // Assert
        assertThat(resultado).isEqualTo(existen);
        verify(outputPort).existenPorEvaluacionesCuantitativas(evaluaciones);
    }
}
