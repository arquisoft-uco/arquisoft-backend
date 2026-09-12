package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCualitativasJuradoUnicasRuleImplTest {

    private final EvaluacionesCualitativasJuradoUnicasRuleImpl regla = new EvaluacionesCualitativasJuradoUnicasRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunItemSolicitadoFueRegistradoAntes() {
        // Arrange
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(UUID.randomUUID()), Set.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConDuplicados_cuandoUnItemSolicitadoYaFueRegistrado() {
        // Arrange
        UUID itemYaRegistrado = UUID.randomUUID();
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(itemYaRegistrado, UUID.randomUUID()), Set.of(itemYaRegistrado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOfSatisfying(EvaluacionesCualitativasJuradoDuplicadasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.ITEMS_YA_REGISTRADOS);
                    assertThat(exception.getMessage()).contains(itemYaRegistrado.toString());
                });
    }
}
