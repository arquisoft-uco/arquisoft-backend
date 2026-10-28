package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoDuplicadoException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.DisponibilidadPlanEstimado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanEstimadoUnicoPorTareaRuleImplTest {

    private static final String DESCRIPCION = "Redactar el capítulo uno";

    private final PlanEstimadoUnicoPorTareaRuleImpl rule = new PlanEstimadoUnicoPorTareaRuleImpl();

    @Test
    void debeLanzarDuplicado_cuandoTareaYaTienePlan() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadPlanEstimado(tarea, DESCRIPCION, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOfSatisfying(PlanEstimadoDuplicadoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DUPLICADO);
                    assertThat(excepcion.getError().getMessage())
                            .contains(DESCRIPCION)
                            .doesNotContain(tarea.toString());
                    assertThat(excepcion.getMessage()).contains(tarea.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoTareaSinPlan() {
        // Arrange
        var disponibilidad = new DisponibilidadPlanEstimado(UtilUUID.generarNuevoUUID(), DESCRIPCION, false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
