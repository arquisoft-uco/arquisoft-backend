package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionExistenteRuleImplTest {

    private final EvaluacionExistenteRuleImpl regla = new EvaluacionExistenteRuleImpl();

    @Test
    void debePermitirFlujo_cuandoLaEvaluacionExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaEvaluacion(UtilUUID.generarNuevoUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaEvaluacion(evaluacion, false)))
                .isInstanceOfSatisfying(EvaluacionNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.Evaluacion.NO_ENCONTRADA);
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                });
    }
}
