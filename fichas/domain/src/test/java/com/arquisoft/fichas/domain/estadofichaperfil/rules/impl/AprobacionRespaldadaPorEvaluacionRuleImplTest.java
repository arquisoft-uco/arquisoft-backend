package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.AprobacionSinEvaluacionAprobatoriaException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.RespaldoAprobacionFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AprobacionRespaldadaPorEvaluacionRuleImplTest {

    private final AprobacionRespaldadaPorEvaluacionRuleImpl regla = new AprobacionRespaldadaPorEvaluacionRuleImpl();

    private static ResumenEvaluacionesFicha resumenSoloNoAprobadas() {
        return new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 2, 0)));
    }

    @Test
    void debeLanzarExcepcion_cuandoAceptaYNingunaEvaluacionEsAprobatoria() {
        // Arrange
        var respaldo = new RespaldoAprobacionFicha(true, resumenSoloNoAprobadas());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(respaldo))
                .isInstanceOf(AprobacionSinEvaluacionAprobatoriaException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.APROBACION_SIN_EVALUACION_APROBATORIA);
    }

    @Test
    void debePasar_cuandoAceptaYHayUnaEvaluacionAprobadaConObservaciones() {
        // Arrange
        var resumen = new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 2, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA_CON_OBSERVACIONES, 1, 1)));
        var respaldo = new RespaldoAprobacionFicha(true, resumen);

        // Act & Assert
        assertThatCode(() -> regla.validar(respaldo)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoNoAceptaAunqueNingunaEvaluacionSeaAprobatoria() {
        // Arrange
        var respaldo = new RespaldoAprobacionFicha(false, resumenSoloNoAprobadas());

        // Act & Assert
        assertThatCode(() -> regla.validar(respaldo)).doesNotThrowAnyException();
    }
}
