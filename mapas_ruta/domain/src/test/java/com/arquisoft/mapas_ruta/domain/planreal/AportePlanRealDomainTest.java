package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class AportePlanRealDomainTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        var aporte = AportePlanRealDomain.crear(plan, "  Priorizar el marco teorico  ");

        // Assert
        assertThat(aporte.getPlanReal()).isEqualTo(plan);
        assertThat(aporte.getDescripcionReal()).isEqualTo("Priorizar el marco teorico");
    }

    @Test
    void debeAcumularFieldErrors_cuandoPlanNuloYDescripcionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> AportePlanRealDomain.crear(null, "   "))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanReal.PLAN_REAL,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO),
                                        tuple(MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeLanzarValidacion_cuandoDescripcionExcedeMaximoTrasRecortar() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> AportePlanRealDomain.crear(UtilUUID.generarNuevoUUID(), " " + larga + " "))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(
                                        tuple(MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA)));
    }
}
