package com.arquisoft.mapas_ruta.domain.planreal.rules.impl;

import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planreal.model.ExistenciaPlanReal;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanRealExisteRuleImplTest {

    private final PlanRealExisteRuleImpl rule = new PlanRealExisteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var existencia = new ExistenciaPlanReal(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarPlanRealNoEncontrado_cuandoNoExiste() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaPlanReal(plan, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(PlanRealNoEncontradoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(MapasRutaCodes.PlanReal.PLAN_REAL_NO_ENCONTRADO);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(plan.toString());
                    assertThat(excepcion.getMessage()).contains(plan.toString());
                });
    }
}
