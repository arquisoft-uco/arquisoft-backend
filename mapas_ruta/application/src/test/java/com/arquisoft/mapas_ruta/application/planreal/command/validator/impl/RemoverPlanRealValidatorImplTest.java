package com.arquisoft.mapas_ruta.application.planreal.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverPlanRealValidatorImplTest {

    private final RemoverPlanRealValidatorImpl validator = new RemoverPlanRealValidatorImpl();

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var remocion = RemocionPlanRealDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(remocion, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarPlanRealNoEncontradoException_cuandoPlanNoExiste() {
        // Arrange
        var remocion = RemocionPlanRealDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, false))
                .isInstanceOf(PlanRealNoEncontradoException.class);
    }
}
