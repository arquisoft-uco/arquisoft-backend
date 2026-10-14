package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoConObservacionesException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.OmisionEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoOmisionEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirEvaluacionesCuantitativasJuradoValidatorImplTest {

    private final OmitirEvaluacionesCuantitativasJuradoValidatorImpl validator =
            new OmitirEvaluacionesCuantitativasJuradoValidatorImpl();

    @Test
    void debePermitirFlujo_cuandoLaEvaluacionExisteContieneLosIdsNoEstaFinalizadaYNoHayObservaciones() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of(evaluacion)),
                new EstadoOmisionEvaluacionesCuantitativasJurado(evaluacionJurado, EstadoEvaluacion.EN_PROGRESO),
                new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarPorExistencia_cuandoLaEvaluacionNoExisteAunqueFallenTodasLasDemasReglas() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, false),
                new ExistenciaEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of()),
                new EstadoOmisionEvaluacionesCuantitativasJurado(evaluacionJurado, EstadoEvaluacion.FINALIZADA),
                new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), true)))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }

    @Test
    void debeRechazarPorPertenencia_cuandoFaltanIdsAunqueLaEvaluacionEstePorFinalizarYTengaObservaciones() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of()),
                new EstadoOmisionEvaluacionesCuantitativasJurado(evaluacionJurado, EstadoEvaluacion.FINALIZADA),
                new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), true)))
                .isInstanceOf(EvaluacionesCuantitativasJuradoNoEncontradasException.class);
    }

    @Test
    void debeRechazarPorEstado_cuandoLaEvaluacionEstaFinalizadaAunqueTengaObservaciones() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of(evaluacion)),
                new EstadoOmisionEvaluacionesCuantitativasJurado(evaluacionJurado, EstadoEvaluacion.FINALIZADA),
                new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), true)))
                .isInstanceOf(OmisionEvaluacionJuradoFinalizadaException.class);
    }

    @Test
    void debeRechazarPorObservaciones_cuandoLasReglasAnterioresPasanYExistenObservaciones() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of(evaluacion)),
                new EstadoOmisionEvaluacionesCuantitativasJurado(evaluacionJurado, EstadoEvaluacion.EN_PROGRESO),
                new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(evaluacion), true)))
                .isInstanceOf(EvaluacionesCuantitativasJuradoConObservacionesException.class);
    }
}
