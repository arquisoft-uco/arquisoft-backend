package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosConEvaluacionesException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.EvaluacionesRegistradasJurados;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradosSinEvaluacionesRuleImplTest {

    private final JuradosSinEvaluacionesRuleImpl regla = new JuradosSinEvaluacionesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunJuradoRegistroEvaluaciones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new EvaluacionesRegistradasJurados(Set.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConDetalle_cuandoAlgunJuradoRegistroEvaluaciones() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new EvaluacionesRegistradasJurados(Set.of(jurado))))
                .isInstanceOfSatisfying(JuradosConEvaluacionesException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.JURADOS_CON_EVALUACIONES);
                    assertThat(exception.getMessage()).contains(jurado.toString());
                    assertThat(exception.getError().getMessage()).doesNotContain(jurado.toString());
                });
    }
}
