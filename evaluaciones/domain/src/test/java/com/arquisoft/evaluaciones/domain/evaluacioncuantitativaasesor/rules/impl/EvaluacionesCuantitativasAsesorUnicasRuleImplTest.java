package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionesCuantitativasAsesorDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.DisponibilidadEvaluacionesCuantitativasAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCuantitativasAsesorUnicasRuleImplTest {

    private final EvaluacionesCuantitativasAsesorUnicasRuleImpl regla = new EvaluacionesCuantitativasAsesorUnicasRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunItemEstaRegistrado() {
        // Arrange
        var entrada = new DisponibilidadEvaluacionesCuantitativasAsesor(
                UtilUUID.generarNuevoUUID(), Set.of(UtilUUID.generarNuevoUUID()), Set.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoAlgunItemYaEstaRegistrado() {
        // Arrange
        var duplicado = UtilUUID.generarNuevoUUID();
        var entrada = new DisponibilidadEvaluacionesCuantitativasAsesor(
                UtilUUID.generarNuevoUUID(), Set.of(duplicado, UtilUUID.generarNuevoUUID()), Set.of(duplicado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionesCuantitativasAsesorDuplicadasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEMS_YA_REGISTRADOS);
                    assertThat(exception.getMessage()).contains(duplicado.toString());
                });
    }
}
