package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneracionPlanRealDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var finalizadaEn = Instant.parse("2026-11-05T10:00:00Z");

        // Act
        var generacion = GeneracionPlanRealDomain.crear(tarea, finalizadaEn);

        // Assert
        assertThat(generacion.getTarea()).isEqualTo(tarea);
        assertThat(generacion.getFinalizadaEn()).isEqualTo(finalizadaEn);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> GeneracionPlanRealDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_REQUERIDA,
                                        MapasRutaCodes.PlanReal.PLAN_REAL_FINALIZADA_EN_REQUERIDA));
    }
}
