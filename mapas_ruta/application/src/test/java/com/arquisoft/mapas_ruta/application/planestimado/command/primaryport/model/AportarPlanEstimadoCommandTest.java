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

class AportarPlanEstimadoCommandTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        var command = AportarPlanEstimadoCommand.crear(plan, "  Revisar  ");

        // Assert
        assertThat(command.planEstimado()).isEqualTo(plan);
        assertThat(command.descripcionEstimado()).isEqualTo("Revisar");
    }

    @Test
    void debeAcumularErrores_cuandoPlanNuloYDescripcionNula() {
        // Act & Assert
        assertThatThrownBy(() -> AportarPlanEstimadoCommand.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO),
                                        tuple(MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeLanzarEntrada_cuandoDescripcionExcedeMaximo() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> AportarPlanEstimadoCommand.crear(UtilUUID.generarNuevoUUID(), larga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(
                                        tuple(MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA)));
    }
}
