package com.arquisoft.mapas_ruta.domain.planestimado;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ModificacionPlanEstimadoDomainTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionPlanEstimadoDomain.crear(plan, "  Nueva descripcion  ");

        // Assert
        assertThat(modificacion.getPlanEstimado()).isEqualTo(plan);
        assertThat(modificacion.getDescripcionEstimado()).isEqualTo("Nueva descripcion");
    }

    @Test
    void debeDejarDescripcionVacia_cuandoNulaOEnBlanco() {
        // Act
        var sinDescripcion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), null);
        var enBlanco = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "   ");

        // Assert
        assertThat(sinDescripcion.getDescripcionEstimado()).isEqualTo(UtilTexto.VACIO);
        assertThat(enBlanco.getDescripcionEstimado()).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoPlanNuloYDescripcionDemasiadoLarga() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionPlanEstimadoDomain.crear(null, larga))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO),
                                        tuple(MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                                                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA)));
    }
}
