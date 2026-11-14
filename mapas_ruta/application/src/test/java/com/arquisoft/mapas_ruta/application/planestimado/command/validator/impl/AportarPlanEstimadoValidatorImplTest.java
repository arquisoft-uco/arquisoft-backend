package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AportarPlanEstimadoValidatorImplTest {

    private final AportarPlanEstimadoValidatorImpl validator = new AportarPlanEstimadoValidatorImpl();

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var aporte = AportePlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatCode(() -> validator.validar(aporte, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoPlanNoExiste() {
        // Arrange
        var aporte = AportePlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(aporte, false))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }
}
