package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuradosAsignadosFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private JuradosAsignadosFinderImpl finder;

    @Test
    void debeDevolverLosJuradosYaAsignados_cuandoSeConsultaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        var libre = UtilUUID.generarNuevoUUID();
        var asignados = Set.of(asignado);
        when(outputPort.consultarJuradosAsignados(evaluacion, Set.of(asignado, libre))).thenReturn(asignados);

        // Act
        var resultado = finder.obtener(new CriterioJuradosEvaluacion(evaluacion, Set.of(asignado, libre)));

        // Assert
        assertThat(resultado).isSameAs(asignados);
    }
}
