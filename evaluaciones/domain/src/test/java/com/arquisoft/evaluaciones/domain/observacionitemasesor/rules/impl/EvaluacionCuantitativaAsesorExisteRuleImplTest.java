package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaEvaluacionCuantitativaAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaAsesorExisteRuleImplTest {

    private final EvaluacionCuantitativaAsesorExisteRuleImpl rule = new EvaluacionCuantitativaAsesorExisteRuleImpl();

    @Test
    void noDebeLanzar_cuandoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionCuantitativaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaEvaluacionCuantitativaAsesor(evaluacion, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(EvaluacionCuantitativaAsesorNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_NO_ENCONTRADA);
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                });
    }
}
