package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionesCualitativasAsesorDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.DisponibilidadEvaluacionesCualitativasAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCualitativasAsesorUnicasRuleImplTest {

    private final EvaluacionesCualitativasAsesorUnicasRuleImpl regla = new EvaluacionesCualitativasAsesorUnicasRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunItemEstaRegistrado() {
        // Arrange
        var entrada = new DisponibilidadEvaluacionesCualitativasAsesor(
                UtilUUID.generarNuevoUUID(), Set.of(UtilUUID.generarNuevoUUID()), Set.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoAlgunItemYaEstaRegistrado() {
        // Arrange
        var duplicado = UtilUUID.generarNuevoUUID();
        var entrada = new DisponibilidadEvaluacionesCualitativasAsesor(
                UtilUUID.generarNuevoUUID(), Set.of(duplicado, UtilUUID.generarNuevoUUID()), Set.of(duplicado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionesCualitativasAsesorDuplicadasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEMS_YA_REGISTRADOS);
                    assertThat(exception.getMessage()).contains(duplicado.toString());
                });
    }
}
