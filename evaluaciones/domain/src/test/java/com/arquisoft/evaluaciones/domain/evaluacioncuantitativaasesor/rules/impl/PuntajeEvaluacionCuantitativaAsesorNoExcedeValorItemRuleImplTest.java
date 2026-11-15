package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajeVsValorMaximoItemAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRuleImplTest {

    private final PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRuleImpl regla =
            new PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRuleImpl();

    @Test
    void debeLanzar_cuandoPuntajeExcedeValor() {
        // Arrange
        var entrada = new PuntajeVsValorMaximoItemAsesor(301, 300, "Rigor");

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException.class,
                        exception -> {
                            assertThat(exception.getCodigoError()).isEqualTo(
                                    EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_EXCEDE_VALOR_ITEM);
                            assertThat(exception.getError().getMessage()).contains("Rigor");
                        });
    }

    @Test
    void debePasar_cuandoPuntajeNoExcedeValor() {
        // Arrange
        var igual = new PuntajeVsValorMaximoItemAsesor(300, 300, "Rigor");
        var menor = new PuntajeVsValorMaximoItemAsesor(299, 300, "Rigor");

        // Act & Assert
        assertThatCode(() -> regla.validar(igual)).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(menor)).doesNotThrowAnyException();
    }
}
