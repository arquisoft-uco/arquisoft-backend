package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.DisponibilidadDescripcionObservacionItemJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescripcionObservacionItemJuradoUnicaRuleImplTest {

    private final DescripcionObservacionItemJuradoUnicaRuleImpl rule =
            new DescripcionObservacionItemJuradoUnicaRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaDescripcionNoExisteAun() {
        // Arrange
        var disponibilidad = new DisponibilidadDescripcionObservacionItemJurado(
                UUID.randomUUID(), "Descripción nueva", false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDescripcionDuplicada_cuandoYaExisteParaEsaEvaluacion() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var disponibilidad = new DisponibilidadDescripcionObservacionItemJurado(
                evaluacionCuantitativaJurado, "Descripción repetida", true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOfSatisfying(DescripcionObservacionItemJuradoDuplicadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DUPLICADA);
                    assertThat(exception.getMessage()).contains(evaluacionCuantitativaJurado.toString());
                });
    }
}
