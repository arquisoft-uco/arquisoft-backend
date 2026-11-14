package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CambioCriterioEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CriteriosCualitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionCualitativaAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarCriterioEvaluacionCualitativaAsesorValidatorImplTest {

    private final CambiarCriterioEvaluacionCualitativaAsesorValidatorImpl validator =
            new CambiarCriterioEvaluacionCualitativaAsesorValidatorImpl();

    private final UUID evaluacionCualitativa = UtilUUID.generarNuevoUUID();
    private final UUID evaluacionAsesor = UtilUUID.generarNuevoUUID();
    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID criterio = UtilUUID.generarNuevoUUID();

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(existencia(true, true), propiedad(asesor),
                estado(EstadoEvaluacion.EN_PROGRESO), criterioExistente()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia(false, false), propiedad(asesor),
                estado(EstadoEvaluacion.PENDIENTE), criterioExistente()))
                .isInstanceOf(EvaluacionCualitativaAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoEsDeOtroAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia(true, true),
                propiedad(UtilUUID.generarNuevoUUID()), estado(EstadoEvaluacion.PENDIENTE), criterioExistente()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarFinalizada_cuandoEvaluacionFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia(true, true), propiedad(asesor),
                estado(EstadoEvaluacion.FINALIZADA), criterioExistente()))
                .isInstanceOf(CambioCriterioEvaluacionCualitativaAsesorFinalizadaException.class);
    }

    @Test
    void debeLanzarCriterioNoEncontrado_cuandoCriterioNoExiste() {
        // Arrange
        var criterioInexistente = new ExistenciaCriteriosCualitativosAsesor(Set.of(criterio), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia(true, true), propiedad(asesor),
                estado(EstadoEvaluacion.PENDIENTE), criterioInexistente))
                .isInstanceOf(CriteriosCualitativosAsesorNoEncontradosException.class);
    }

    @Test
    void debeRespetarElOrden_cuandoVariasReglasFallan() {
        // Arrange
        var criterioInexistente = new ExistenciaCriteriosCualitativosAsesor(Set.of(criterio), Set.of());
        var otroAsesor = propiedad(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia(false, true), otroAsesor,
                estado(EstadoEvaluacion.FINALIZADA), criterioInexistente))
                .isInstanceOf(EvaluacionCualitativaAsesorNoEncontradaException.class);
        assertThatThrownBy(() -> validator.validar(existencia(true, true), otroAsesor,
                estado(EstadoEvaluacion.FINALIZADA), criterioInexistente))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
        assertThatThrownBy(() -> validator.validar(existencia(true, true), propiedad(asesor),
                estado(EstadoEvaluacion.FINALIZADA), criterioInexistente))
                .isInstanceOf(CambioCriterioEvaluacionCualitativaAsesorFinalizadaException.class);
    }

    private ExistenciaEvaluacionCualitativaAsesor existencia(boolean contexto, boolean registro) {
        return new ExistenciaEvaluacionCualitativaAsesor(evaluacionCualitativa, contexto, registro);
    }

    private PropiedadEvaluacionAsesor propiedad(UUID solicitante) {
        return new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, solicitante);
    }

    private EstadoCambioCriterioEvaluacionCualitativaAsesor estado(EstadoEvaluacion estado) {
        return new EstadoCambioCriterioEvaluacionCualitativaAsesor(evaluacionAsesor, estado);
    }

    private ExistenciaCriteriosCualitativosAsesor criterioExistente() {
        return new ExistenciaCriteriosCualitativosAsesor(Set.of(criterio), Set.of(criterio));
    }
}
