package com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesDeEvaluacionAsesorQueryFinderImplTest {

    @Mock
    private EvaluacionAsesorAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EstudiantesDeEvaluacionAsesorQueryFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoSeObtiene() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(outputPort.obtenerEstudiantesVigentesPorId(evaluacionAsesor)).thenReturn(estudiantes);

        // Act
        var resultado = finder.obtener(evaluacionAsesor);

        // Assert
        assertThat(resultado).isSameAs(estudiantes);
    }
}
