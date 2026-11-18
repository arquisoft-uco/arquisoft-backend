package com.arquisoft.mapas_ruta.application.planreal.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarPlanRealValidatorImplTest {

    private final ModificarPlanRealValidatorImpl validator = new ModificarPlanRealValidatorImpl();

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var modificacion = ModificacionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), 2, "Texto");

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoPlanNoExiste() {
        // Arrange
        var modificacion = ModificacionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), 2, "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false))
                .isInstanceOf(PlanRealNoEncontradoException.class);
    }
}
