package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ModificarPlanEstimadoCommandTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarPlanEstimadoCommand.crear(plan, "  Revisar  ");

        // Assert
        assertThat(command.planEstimado()).isEqualTo(plan);
        assertThat(command.descripcionEstimado()).isEqualTo("Revisar");
    }

    @Test
    void debeAcumularErrores_cuandoPlanNuloYDescripcionDemasiadoLarga() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ModificarPlanEstimadoCommand.crear(null, larga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO),
                                        tuple(MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA)));
    }
}
