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

class AgregarPlanEstimadoCommandTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarPlanEstimadoCommand.crear(tarea, 5, "  Revisar  ");

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.tiempoEstimadoDias()).isEqualTo(5);
        assertThat(command.descripcionEstimado()).isEqualTo("Revisar");
    }

    @Test
    void debeCrear_cuandoDescripcionNoSeEnvia() {
        // Act
        var command = AgregarPlanEstimadoCommand.crear(UtilUUID.generarNuevoUUID(), 1, null);

        // Assert
        assertThat(command.tiempoEstimadoDias()).isEqualTo(1);
    }

    @Test
    void debeAcumularErrores_cuandoTareaTiempoYDescripcionInvalidos() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> AgregarPlanEstimadoCommand.crear(null, null, larga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanEstimado.TAREA,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TAREA_REQUERIDA),
                                        tuple(MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_REQUERIDO),
                                        tuple(MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA)));
    }

    @Test
    void debeLanzarTiempoInvalido_cuandoTiempoEsMenorAUno() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AgregarPlanEstimadoCommand.crear(tarea, 0, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                                        MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_INVALIDO)));
    }
}
