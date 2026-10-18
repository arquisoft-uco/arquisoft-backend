package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirEvaluacionesCualitativasJuradoValidatorImplTest {

    private final OmitirEvaluacionesCualitativasJuradoValidatorImpl validator =
            new OmitirEvaluacionesCualitativasJuradoValidatorImpl();

    @Test
    void debePermitirFlujo_cuandoLaEvaluacionExisteContieneLosIdsYNoEstaFinalizada() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCualitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of(evaluacion)),
                new EstadoOmisionEvaluacionesCualitativasJurado(evaluacionJurado, EstadoEvaluacion.EN_PROGRESO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarPorExistencia_cuandoLaEvaluacionNoExisteAunqueFaltenIdsYEstePorFinalizar() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, false),
                new ExistenciaEvaluacionesCualitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of()),
                new EstadoOmisionEvaluacionesCualitativasJurado(evaluacionJurado, EstadoEvaluacion.FINALIZADA)))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }

    @Test
    void debeRechazarPorPertenencia_cuandoFaltanIdsAunqueLaEvaluacionEstePorFinalizar() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCualitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of()),
                new EstadoOmisionEvaluacionesCualitativasJurado(evaluacionJurado, EstadoEvaluacion.FINALIZADA)))
                .isInstanceOf(EvaluacionesCualitativasJuradoNoEncontradasException.class);
    }

    @Test
    void debeRechazarPorEstado_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, true),
                new ExistenciaEvaluacionesCualitativasJurado(evaluacionJurado, Set.of(evaluacion), Set.of(evaluacion)),
                new EstadoOmisionEvaluacionesCualitativasJurado(evaluacionJurado, EstadoEvaluacion.FINALIZADA)))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
    }
}
