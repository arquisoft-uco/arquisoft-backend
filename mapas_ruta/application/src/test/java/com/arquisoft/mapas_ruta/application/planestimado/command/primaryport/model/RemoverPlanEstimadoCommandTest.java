package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class RemoverPlanEstimadoCommandTest {

    @Test
    void debeCrear_cuandoPlanEstimadoValido() {
        // Arrange
        var planEstimado = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverPlanEstimadoCommand.crear(planEstimado);

        // Assert
        assertThat(command.planEstimado()).isEqualTo(planEstimado);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoPlanEstimadoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverPlanEstimadoCommand.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                                        MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO)));
    }
}
