package com.arquisoft.mapas_ruta.application.planreal.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AportarPlanRealValidatorImplTest {

    private final AportarPlanRealValidatorImpl validator = new AportarPlanRealValidatorImpl();

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var aporte = AportePlanRealDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatCode(() -> validator.validar(aporte, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoPlanNoExiste() {
        // Arrange
        var aporte = AportePlanRealDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(aporte, false))
                .isInstanceOf(PlanRealNoEncontradoException.class);
    }
}
