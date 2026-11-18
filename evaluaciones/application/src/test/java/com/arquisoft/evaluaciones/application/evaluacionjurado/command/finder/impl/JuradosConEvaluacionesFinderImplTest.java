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
class JuradosConEvaluacionesFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private JuradosConEvaluacionesFinderImpl finder;

    @Test
    void debeDevolverLosJuradosConEvaluaciones_cuandoSeConsultaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var conEvaluaciones = UtilUUID.generarNuevoUUID();
        var sinEvaluaciones = UtilUUID.generarNuevoUUID();
        var esperados = Set.of(conEvaluaciones);
        when(outputPort.consultarJuradosConEvaluaciones(evaluacion, Set.of(conEvaluaciones, sinEvaluaciones)))
                .thenReturn(esperados);

        // Act
        var resultado = finder.obtener(new CriterioJuradosEvaluacion(evaluacion, Set.of(conEvaluaciones, sinEvaluaciones)));

        // Assert
        assertThat(resultado).isSameAs(esperados);
    }
}
