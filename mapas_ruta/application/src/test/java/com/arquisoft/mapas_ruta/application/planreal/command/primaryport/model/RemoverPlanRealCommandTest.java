package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class RemoverPlanRealCommandTest {

    @Test
    void debeCrear_cuandoPlanRealValido() {
        // Arrange
        var planReal = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverPlanRealCommand.crear(planReal);

        // Assert
        assertThat(command.planReal()).isEqualTo(planReal);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoPlanRealEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverPlanRealCommand.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanReal.PLAN_REAL,
                                        MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO)));
    }
}
