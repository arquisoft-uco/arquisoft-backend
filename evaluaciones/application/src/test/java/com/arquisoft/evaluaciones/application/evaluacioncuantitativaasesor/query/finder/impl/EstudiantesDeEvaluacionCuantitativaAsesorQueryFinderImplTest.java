package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorAccesoQueryOutputPort;
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
class EstudiantesDeEvaluacionCuantitativaAsesorQueryFinderImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EstudiantesDeEvaluacionCuantitativaAsesorQueryFinderImpl finder;

    @Test
    void debeDevolverLosEstudiantes_cuandoHayVinculados() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(outputPort.obtenerEstudiantesVigentesPorId(id)).thenReturn(estudiantes);

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(estudiantes);
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayVinculados() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerEstudiantesVigentesPorId(id)).thenReturn(List.of());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
