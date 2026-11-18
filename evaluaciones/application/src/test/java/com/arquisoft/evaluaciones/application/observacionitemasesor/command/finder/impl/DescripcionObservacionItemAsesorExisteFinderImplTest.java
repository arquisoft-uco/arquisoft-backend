package com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.model.CriterioDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescripcionObservacionItemAsesorExisteFinderImplTest {

    @Mock
    private ObservacionItemAsesorOutputPort outputPort;

    @InjectMocks
    private DescripcionObservacionItemAsesorExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.existePorEvaluacionYDescripcion(evaluacion, "Sustento")).thenReturn(true);

        // Act
        var resultado = finder.obtener(new CriterioDescripcionObservacionItemAsesor(evaluacion, "Sustento"));

        // Assert
        assertThat(resultado).isTrue();
    }
}
