package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CambioCriterioEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriterioEvaluacionCualitativaJuradoSinCambioException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriteriosCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionCualitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarCriterioEvaluacionCualitativaJuradoValidatorImplTest {

    private final CambiarCriterioEvaluacionCualitativaJuradoValidatorImpl validator =
            new CambiarCriterioEvaluacionCualitativaJuradoValidatorImpl();

    private final UUID id = UtilUUID.generarNuevoUUID();
    private final UUID evaluacionJurado = UtilUUID.generarNuevoUUID();
    private final UUID item = UtilUUID.generarNuevoUUID();
    private final UUID jurado = UtilUUID.generarNuevoUUID();
    private final UUID criterioActual = UtilUUID.generarNuevoUUID();
    private final UUID criterioNuevo = UtilUUID.generarNuevoUUID();

    private CambioCriterioEvaluacionCualitativaJuradoDomain cambio() {
        return CambioCriterioEvaluacionCualitativaJuradoDomain.crear(id, jurado, criterioNuevo);
    }

    private EvaluacionCualitativaJuradoDomain evaluacion() {
        return EvaluacionCualitativaJuradoDomain.reconstruir(id, evaluacionJurado, item, criterioActual);
    }

    private EstadoEvaluacionJuradoDomain estado(UUID juradoAsignado, EstadoEvaluacion estado) {
        return EstadoEvaluacionJuradoDomain.reconstruir(evaluacionJurado, juradoAsignado, estado);
    }

    @Test
    void debePermitirFlujo_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                cambio(), evaluacion(), estado(jurado, EstadoEvaluacion.EN_PROGRESO), Set.of(criterioNuevo)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarPorExistencia_cuandoLaEvaluacionNoExisteAunqueElRestoFalle() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(), EvaluacionCualitativaJuradoDomain.VACIO,
                estado(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.FINALIZADA), Set.of()))
                .isInstanceOf(EvaluacionCualitativaJuradoNoEncontradaException.class);
    }

    @Test
    void debeRechazarPorCriterioInexistente_cuandoElCriterioNoExisteAunqueSeaAjenaYFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(), evaluacion(), estado(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.FINALIZADA), Set.of()))
                .isInstanceOf(CriteriosCualitativosJuradoNoEncontradosException.class);
    }

    @Test
    void debeRechazarPorPropiedad_cuandoLaEvaluacionEsDeOtroJuradoAunqueEsteFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(), evaluacion(), estado(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.FINALIZADA),
                Set.of(criterioNuevo)))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
    }

    @Test
    void debeRechazarPorEstado_cuandoLaEvaluacionJuradoEstaFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(), evaluacion(), estado(jurado, EstadoEvaluacion.FINALIZADA), Set.of(criterioNuevo)))
                .isInstanceOf(CambioCriterioEvaluacionJuradoFinalizadaException.class);
    }

    @Test
    void debeRechazarPorSinCambio_cuandoElCriterioNuevoEsElActual() {
        // Arrange
        var sinCambio = CambioCriterioEvaluacionCualitativaJuradoDomain.crear(id, jurado, criterioActual);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                sinCambio, evaluacion(), estado(jurado, EstadoEvaluacion.PENDIENTE), Set.of(criterioActual)))
                .isInstanceOf(CriterioEvaluacionCualitativaJuradoSinCambioException.class);
    }
}
