package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosEvaluadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosEvaluadosEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradosPendientesPorEvaluarRuleImplTest {

    private final JuradosPendientesPorEvaluarRuleImpl regla = new JuradosPendientesPorEvaluarRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunJuradoCompletoSuEvaluacion() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new JuradosEvaluadosEvaluacion(Set.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConDetalle_cuandoAlgunJuradoYaEstaEvaluado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new JuradosEvaluadosEvaluacion(Set.of(jurado))))
                .isInstanceOfSatisfying(JuradosEvaluadosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.JURADOS_EVALUADOS);
                    assertThat(exception.getMessage()).contains(jurado.toString());
                    assertThat(exception.getError().getMessage()).doesNotContain(jurado.toString());
                });
    }
}
