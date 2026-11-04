package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.ExistenciaPlanEstimado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanEstimadoExisteRuleImplTest {

    private final PlanEstimadoExisteRuleImpl rule = new PlanEstimadoExisteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var existencia = new ExistenciaPlanEstimado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarPlanEstimadoNoEncontrado_cuandoNoExiste() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaPlanEstimado(plan, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(PlanEstimadoNoEncontradoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_NO_ENCONTRADO);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(plan.toString());
                    assertThat(excepcion.getMessage()).contains(plan.toString());
                });
    }
}
