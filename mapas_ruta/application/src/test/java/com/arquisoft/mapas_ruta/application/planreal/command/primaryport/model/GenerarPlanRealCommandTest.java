package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class GenerarPlanRealCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var finalizadaEn = Instant.parse("2026-11-05T10:00:00Z");

        // Act
        var command = GenerarPlanRealCommand.crear(tarea.toString(), finalizadaEn);

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.finalizadaEn()).isEqualTo(finalizadaEn);
    }

    @Test
    void debeAcumularErrores_cuandoTareaNoEsUuidYFaltaFinalizadaEn() {
        // Act & Assert
        assertThatThrownBy(() -> GenerarPlanRealCommand.crear("no-es-uuid", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanReal.TAREA,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_INVALIDA),
                                        tuple(MapasRutaFields.PlanReal.FINALIZADA_EN,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_FINALIZADA_EN_REQUERIDA)));
    }
}
