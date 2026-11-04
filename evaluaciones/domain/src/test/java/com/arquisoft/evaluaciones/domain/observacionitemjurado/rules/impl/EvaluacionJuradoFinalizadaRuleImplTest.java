package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.EstadoEvaluacionJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoFinalizadaRuleImplTest {

    private final EvaluacionJuradoFinalizadaRuleImpl rule = new EvaluacionJuradoFinalizadaRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionDeJuradoNoEstaFinalizada() {
        // Arrange
        var estado = new EstadoEvaluacionJurado(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEvaluacionFinalizada_cuandoLaEvaluacionDeJuradoEstaFinalizada() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var estado = new EstadoEvaluacionJurado(evaluacionCuantitativaJurado, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estado))
                .isInstanceOfSatisfying(EvaluacionJuradoFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.EVALUACION_JURADO_FINALIZADA);
                    assertThat(exception.getMessage()).contains(evaluacionCuantitativaJurado.toString());
                });
    }
}
