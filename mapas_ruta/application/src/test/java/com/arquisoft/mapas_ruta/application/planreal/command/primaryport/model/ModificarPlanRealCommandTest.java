package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

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

class ModificarPlanRealCommandTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarPlanRealCommand.crear(plan, 3, "  Revisar  ");

        // Assert
        assertThat(command.planReal()).isEqualTo(plan);
        assertThat(command.tiempoRealDias()).isEqualTo(3);
        assertThat(command.descripcionReal()).isEqualTo("Revisar");
    }

    @Test
    void debeAcumularErrores_cuandoPlanTiempoYDescripcionInvalidos() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ModificarPlanRealCommand.crear(null, null, larga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanReal.PLAN_REAL,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO),
                                        tuple(MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_REQUERIDO),
                                        tuple(MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA)));
    }

    @Test
    void debeLanzarTiempoInvalido_cuandoMenorAlMinimo() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var bajoElMinimo = MapasRutaLimits.PlanReal.TIEMPO_REAL_DIAS_MIN - 1;

        // Act & Assert
        assertThatThrownBy(() -> ModificarPlanRealCommand.crear(plan, bajoElMinimo, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(
                                        tuple(MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_INVALIDO)));
    }
}
