package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
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
class PertenenciaObservacionEvaluacionFinderImplTest {

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @InjectMocks
    private PertenenciaObservacionEvaluacionFinderImpl finder;

    private final ModificacionObservacionEvaluacionDomain modificacion = ModificacionObservacionEvaluacionDomain.crear(
            UtilUUID.generarNuevoUUID(), "Nuevo texto de la observación", UtilUUID.generarNuevoUUID());

    @Test
    void debeMapearPertenenciaConSuUltimoEstado_cuandoLaObservacionExiste() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        stubPuerto(Optional.of(new PertenenciaObservacionEvaluacionEntity(
                evaluacionFichaPerfil, true, EstadoEvaluacion.APROBADA.getId())));

        // Act
        var pertenencia = finder.obtener(modificacion);

        // Assert
        assertThat(pertenencia).isEqualTo(
                new PertenenciaObservacionEvaluacion(evaluacionFichaPerfil, true, EstadoEvaluacion.APROBADA));
        assertThat(pertenencia.esVacio()).isFalse();
    }

    @Test
    void debeMapearEstadoVacio_cuandoLaEvaluacionNoTieneEstados() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        stubPuerto(Optional.of(new PertenenciaObservacionEvaluacionEntity(evaluacionFichaPerfil, false, null)));

        // Act
        var pertenencia = finder.obtener(modificacion);

        // Assert
        assertThat(pertenencia.ultimoEstado()).isEqualTo(EstadoEvaluacion.VACIO);
        assertThat(pertenencia.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(pertenencia.esPropietario()).isFalse();
    }

    @Test
    void debeRetornarCentinela_cuandoLaObservacionNoExiste() {
        // Arrange
        stubPuerto(Optional.empty());

        // Act
        var pertenencia = finder.obtener(modificacion);

        // Assert
        assertThat(pertenencia).isSameAs(PertenenciaObservacionEvaluacion.VACIO);
        assertThat(pertenencia.esVacio()).isTrue();
    }

    private void stubPuerto(Optional<PertenenciaObservacionEvaluacionEntity> respuesta) {
        when(observacionEvaluacionOutputPort.obtenerPertenencia(
                modificacion.getObservacionEvaluacion(), modificacion.getRepresentanteComite()))
                .thenReturn(respuesta);
    }
}
