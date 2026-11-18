package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriterioEvaluacionCualitativaJuradoSinCambioException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.CriterioActualVsNuevo;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriterioEvaluacionCualitativaJuradoDistintoRuleImplTest {

    private final CriterioEvaluacionCualitativaJuradoDistintoRuleImpl regla =
            new CriterioEvaluacionCualitativaJuradoDistintoRuleImpl();

    @Test
    void debePermitirFlujo_cuandoElCriterioNuevoEsDistintoDelActual() {
        // Arrange
        var entrada = new CriterioActualVsNuevo(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionSinUuidEnElMensaje_cuandoElCriterioNuevoEsElActual() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var entrada = new CriterioActualVsNuevo(evaluacion, criterio, criterio);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(CriterioEvaluacionCualitativaJuradoSinCambioException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_SIN_CAMBIO);
                    assertThat(exception.getError().getMessage())
                            .doesNotContain(evaluacion.toString(), criterio.toString());
                    assertThat(exception.getMessage()).contains(evaluacion.toString(), criterio.toString());
                });
    }
}
