package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionCualitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionCualitativaJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCualitativaJuradoExistenteRuleImplTest {

    private final EvaluacionCualitativaJuradoExistenteRuleImpl regla = new EvaluacionCualitativaJuradoExistenteRuleImpl();

    @Test
    void debePermitirFlujo_cuandoLaEvaluacionExiste() {
        // Arrange
        var entrada = new ExistenciaEvaluacionCualitativaJurado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionSinUuidEnElMensaje_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaEvaluacionCualitativaJurado(evaluacion, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionCualitativaJuradoNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.NO_ENCONTRADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacion.toString());
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                });
    }
}
