package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.model.CriterioDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescripcionObservacionItemJuradoExisteFinderImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @InjectMocks
    private DescripcionObservacionItemJuradoExisteFinderImpl finder;

    @Test
    void debeRetornarVerdadero_cuandoLaDescripcionYaExiste() {
        // Arrange
        var criterio = new CriterioDescripcionObservacionItemJurado(UUID.randomUUID(), "Descripción repetida");
        when(outputPort.existePorEvaluacionYDescripcion(criterio.evaluacionCuantitativaJurado(), criterio.descripcion()))
                .thenReturn(true);

        // Act
        var resultado = finder.obtener(criterio);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoLaDescripcionNoExiste() {
        // Arrange
        var criterio = new CriterioDescripcionObservacionItemJurado(UUID.randomUUID(), "Descripción nueva");
        when(outputPort.existePorEvaluacionYDescripcion(criterio.evaluacionCuantitativaJurado(), criterio.descripcion()))
                .thenReturn(false);

        // Act
        var resultado = finder.obtener(criterio);

        // Assert
        assertThat(resultado).isFalse();
    }
}
