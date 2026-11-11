package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCuantitativasJuradoUnicasRuleImplTest {

    private final EvaluacionesCuantitativasJuradoUnicasRuleImpl regla = new EvaluacionesCuantitativasJuradoUnicasRuleImpl();

    @Test
    void noDebeLanzar_cuandoNingunItemFueRegistradoAntes() {
        // Arrange
        var entrada = new DisponibilidadEvaluacionesCuantitativasJurado(
                UtilUUID.generarNuevoUUID(), Set.of(UtilUUID.generarNuevoUUID()), Set.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConDuplicados_cuandoUnItemYaFueRegistrado() {
        // Arrange
        var yaRegistrado = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var entrada = new DisponibilidadEvaluacionesCuantitativasJurado(
                UtilUUID.generarNuevoUUID(), Set.of(yaRegistrado, nuevo), Set.of(yaRegistrado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionesCuantitativasJuradoDuplicadasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEMS_YA_REGISTRADOS);
                    assertThat(exception.getMessage()).contains(yaRegistrado.toString()).doesNotContain(nuevo.toString());
                });
    }
}
