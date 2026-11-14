package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionesCualitativasAsesorNoRegistradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.OmisionEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoOmisionEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionesCualitativasAsesorRegistradas;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirEvaluacionesCualitativasAsesorValidatorImplTest {

    private final OmitirEvaluacionesCualitativasAsesorValidatorImpl validator =
            new OmitirEvaluacionesCualitativasAsesorValidatorImpl();

    private final UUID evaluacionAsesor = UtilUUID.generarNuevoUUID();
    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID item = UtilUUID.generarNuevoUUID();

    @Test
    void noDebeLanzar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(existenciaValida(), propiedadValida(), estado(EstadoEvaluacion.PENDIENTE),
                registradasTodas()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEvaluacionNoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, propiedadValida(),
                estado(EstadoEvaluacion.PENDIENTE), registradasTodas()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoEvaluacionDeOtroAsesor() {
        // Arrange
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedad,
                estado(EstadoEvaluacion.PENDIENTE), registradasTodas()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzar_cuandoEvaluacionFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(),
                estado(EstadoEvaluacion.FINALIZADA), registradasTodas()))
                .isInstanceOf(OmisionEvaluacionCualitativaAsesorFinalizadaException.class);
    }

    @Test
    void debeLanzar_cuandoItemNoRegistrado() {
        // Arrange
        var registradas = new ExistenciaEvaluacionesCualitativasAsesorRegistradas(
                evaluacionAsesor, Set.of(item), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(),
                estado(EstadoEvaluacion.PENDIENTE), registradas))
                .isInstanceOf(EvaluacionesCualitativasAsesorNoRegistradasException.class);
    }

    @Test
    void debeDarPrioridadAExistenciaSobrePropiedad_cuandoAmbasFallan() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto());
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, propiedad,
                estado(EstadoEvaluacion.FINALIZADA), registradasTodas()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }

    @Test
    void debeDarPrioridadAPropiedadSobreFinalizada_cuandoAmbasFallan() {
        // Arrange
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedad,
                estado(EstadoEvaluacion.FINALIZADA), registradasTodas()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeDarPrioridadAFinalizadaSobreNoRegistrado_cuandoAmbasFallan() {
        // Arrange
        var registradas = new ExistenciaEvaluacionesCualitativasAsesorRegistradas(
                evaluacionAsesor, Set.of(item), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(),
                estado(EstadoEvaluacion.FINALIZADA), registradas))
                .isInstanceOf(OmisionEvaluacionCualitativaAsesorFinalizadaException.class);
    }

    private ExistenciaEvaluacionAsesor existenciaValida() {
        return new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesor);
    }

    private PropiedadEvaluacionAsesor propiedadValida() {
        return new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, asesor);
    }

    private EstadoOmisionEvaluacionesCualitativasAsesor estado(EstadoEvaluacion estado) {
        return new EstadoOmisionEvaluacionesCualitativasAsesor(evaluacionAsesor, estado);
    }

    private ExistenciaEvaluacionesCualitativasAsesorRegistradas registradasTodas() {
        return new ExistenciaEvaluacionesCualitativasAsesorRegistradas(
                evaluacionAsesor, Set.of(item), Set.of(item));
    }
}
