package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverPlanEstimadoValidatorImplTest {

    private final RemoverPlanEstimadoValidatorImpl validator = new RemoverPlanEstimadoValidatorImpl();

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(remocion, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarPlanEstimadoNoEncontradoException_cuandoPlanNoExiste() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, false))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }
}
