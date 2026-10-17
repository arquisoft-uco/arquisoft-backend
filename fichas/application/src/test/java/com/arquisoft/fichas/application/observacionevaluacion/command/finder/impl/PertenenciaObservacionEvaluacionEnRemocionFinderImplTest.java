package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
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
class PertenenciaObservacionEvaluacionEnRemocionFinderImplTest {

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @InjectMocks
    private PertenenciaObservacionEvaluacionEnRemocionFinderImpl finder;

    private final RemocionObservacionEvaluacionDomain remocion = RemocionObservacionEvaluacionDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

    @Test
    void debeMapearPertenenciaConSuUltimoEstado_cuandoLaObservacionExiste() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        when(observacionEvaluacionOutputPort.obtenerPertenencia(
                remocion.getObservacionEvaluacion(), remocion.getRepresentanteComite()))
                .thenReturn(Optional.of(new PertenenciaObservacionEvaluacionEntity(
                        evaluacionFichaPerfil, true, EstadoEvaluacion.EN_EVALUACION.getId())));

        // Act
        var pertenencia = finder.obtener(remocion);

        // Assert
        assertThat(pertenencia).isEqualTo(
                new PertenenciaObservacionEvaluacion(evaluacionFichaPerfil, true, EstadoEvaluacion.EN_EVALUACION));
        assertThat(pertenencia.esVacio()).isFalse();
    }

    @Test
    void debeRetornarCentinela_cuandoLaObservacionNoExiste() {
        // Arrange
        when(observacionEvaluacionOutputPort.obtenerPertenencia(
                remocion.getObservacionEvaluacion(), remocion.getRepresentanteComite()))
                .thenReturn(Optional.empty());

        // Act
        var pertenencia = finder.obtener(remocion);

        // Assert
        assertThat(pertenencia).isSameAs(PertenenciaObservacionEvaluacion.VACIO);
    }
}
