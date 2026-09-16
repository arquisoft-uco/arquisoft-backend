package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.PropiedadEvaluacionCuantitativaJuradoJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaJuradoPropiedadJuradoRuleImplTest {

    private final EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl rule =
            new EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl();

    @Test
    void noDebeLanzar_cuandoPerteneceAlJurado() {
        // Arrange
        var propiedad = new PropiedadEvaluacionCuantitativaJuradoJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPerteneceJurado_cuandoNoPerteneceAlJurado() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var propiedad = new PropiedadEvaluacionCuantitativaJuradoJurado(evaluacionCuantitativaJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(propiedad))
                .isInstanceOfSatisfying(
                        EvaluacionCuantitativaJuradoNoPerteneceJuradoException.class, exception -> {
                            assertThat(exception.getCodigoError()).isEqualTo(
                                    EvaluacionesCodes.ObservacionItemJurado.EVALUACION_NO_PERTENECE_JURADO);
                            assertThat(exception.getMessage()).contains(evaluacionCuantitativaJurado.toString());
                        });
    }
}
