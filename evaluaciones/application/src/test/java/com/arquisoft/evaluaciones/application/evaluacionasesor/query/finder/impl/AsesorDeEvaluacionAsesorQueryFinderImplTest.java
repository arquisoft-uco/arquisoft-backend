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
class AsesorDeEvaluacionAsesorQueryFinderImplTest {

    @Mock
    private EvaluacionAsesorAccesoQueryOutputPort outputPort;

    @InjectMocks
    private AsesorDeEvaluacionAsesorQueryFinderImpl finder;

    @Test
    void debeRetornarElAsesor_cuandoLaEvaluacionExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorPorId(evaluacionAsesor)).thenReturn(Optional.of(asesor));

        // Act
        var resultado = finder.obtener(evaluacionAsesor);

        // Assert
        assertThat(resultado).isEqualTo(asesor);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorPorId(evaluacionAsesor)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacionAsesor);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
