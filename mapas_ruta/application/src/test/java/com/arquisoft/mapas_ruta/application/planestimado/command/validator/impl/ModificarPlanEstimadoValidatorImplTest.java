package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarPlanEstimadoValidatorImplTest {

    private final ModificarPlanEstimadoValidatorImpl validator = new ModificarPlanEstimadoValidatorImpl();

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var modificacion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoPlanNoExiste() {
        // Arrange
        var modificacion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }
}
