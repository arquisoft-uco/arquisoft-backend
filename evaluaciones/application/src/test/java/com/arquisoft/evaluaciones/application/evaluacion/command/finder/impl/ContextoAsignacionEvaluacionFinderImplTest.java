package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ContextoAsignacionEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.ContextoAsignacionEvaluacionDomain;
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
class ContextoAsignacionEvaluacionFinderImplTest {

    @Mock
    private EvaluacionOutputPort outputPort;

    @InjectMocks
    private ContextoAsignacionEvaluacionFinderImpl finder;

    @Test
    void debeDevolverContextoReconstruido_cuandoLaEvaluacionExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoAsignacionBloqueado(evaluacion)).thenReturn(Optional.of(
                new ContextoAsignacionEvaluacionEntity(evaluacion, EstadoEvaluacion.EN_PROGRESO.getId())));

        // Act
        var contexto = finder.obtener(evaluacion);

        // Assert
        assertThat(contexto.esVacio()).isFalse();
        assertThat(contexto.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(contexto.getEstado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
    }

    @Test
    void debeDevolverVacio_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoAsignacionBloqueado(evaluacion)).thenReturn(Optional.empty());

        // Act
        var contexto = finder.obtener(evaluacion);

        // Assert
        assertThat(contexto).isSameAs(ContextoAsignacionEvaluacionDomain.VACIO);
    }
}
