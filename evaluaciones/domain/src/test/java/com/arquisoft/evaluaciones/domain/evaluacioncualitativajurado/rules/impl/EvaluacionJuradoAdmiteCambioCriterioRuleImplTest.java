package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CambioCriterioEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoCambioCriterioEvaluacionCualitativaJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoAdmiteCambioCriterioRuleImplTest {

    private final EvaluacionJuradoAdmiteCambioCriterioRuleImpl regla = new EvaluacionJuradoAdmiteCambioCriterioRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class, names = {"PENDIENTE", "EN_PROGRESO"})
    void debePermitirFlujo_cuandoLaEvaluacionNoEstaFinalizada(EstadoEvaluacion estado) {
        // Arrange
        var entrada = new EstadoCambioCriterioEvaluacionCualitativaJurado(UtilUUID.generarNuevoUUID(), estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionSinUuidEnElMensaje_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoCambioCriterioEvaluacionCualitativaJurado(evaluacion, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(CambioCriterioEvaluacionJuradoFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCualitativaJurado.CAMBIO_CRITERIO_EVALUACION_JURADO_FINALIZADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacion.toString());
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                });
    }
}
