package com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionAsesorDeEvaluacionQueryFinderImplTest {

    @Mock
    private EvaluacionAsesorAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EvaluacionAsesorDeEvaluacionQueryFinderImpl finder;

    @Test
    void debeDevolverElId_cuandoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorEvaluacion(evaluacion)).thenReturn(Optional.of(evaluacionAsesor));

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(resultado).isEqualTo(evaluacionAsesor);
    }

    @Test
    void debeDevolverUuidPorDefecto_cuandoNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorEvaluacion(evaluacion)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacion);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
