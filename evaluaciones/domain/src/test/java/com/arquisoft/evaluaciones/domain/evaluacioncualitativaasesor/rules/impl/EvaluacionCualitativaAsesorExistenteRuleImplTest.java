package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionCualitativaAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCualitativaAsesorExistenteRuleImplTest {

    private final EvaluacionCualitativaAsesorExistenteRuleImpl regla =
            new EvaluacionCualitativaAsesorExistenteRuleImpl();

    @Test
    void debeLanzar_cuandoNoExisteContexto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaEvaluacionCualitativaAsesor(id, false, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionCualitativaAsesorNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_NO_ENCONTRADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(id.toString());
                });
    }

    @Test
    void debeLanzar_cuandoNoExisteRegistro() {
        // Arrange
        var entrada = new ExistenciaEvaluacionCualitativaAsesor(UtilUUID.generarNuevoUUID(), true, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(EvaluacionCualitativaAsesorNoEncontradaException.class);
    }

    @Test
    void noDebeLanzar_cuandoExisten() {
        // Arrange
        var entrada = new ExistenciaEvaluacionCualitativaAsesor(UtilUUID.generarNuevoUUID(), true, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
