package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.ContextoEvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
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
class ContextoEvaluacionCuantitativaAsesorPorObservacionFinderImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @InjectMocks
    private ContextoEvaluacionCuantitativaAsesorPorObservacionFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoPorObservacion(observacion)).thenReturn(Optional.of(
                new ContextoEvaluacionCuantitativaAsesorEntity(id, evaluacionAsesor, asesor, "EN_PROGRESO")));

        // Act
        var contexto = finder.obtener(observacion);

        // Assert
        assertThat(contexto.esVacio()).isFalse();
        assertThat(contexto.getId()).isEqualTo(id);
        assertThat(contexto.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(contexto.getAsesor()).isEqualTo(asesor);
        assertThat(contexto.getEstado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoPorObservacion(observacion)).thenReturn(Optional.empty());

        // Act
        var contexto = finder.obtener(observacion);

        // Assert
        assertThat(contexto).isSameAs(ContextoEvaluacionCuantitativaAsesorDomain.VACIO);
    }
}
