package com.arquisoft.mapas_ruta.domain.planreal;

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

class ModificacionPlanRealDomainTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionPlanRealDomain.crear(plan, 5, "  Nueva descripcion  ");

        // Assert
        assertThat(modificacion.getPlanReal()).isEqualTo(plan);
        assertThat(modificacion.getTiempoRealDias()).isEqualTo(5);
        assertThat(modificacion.getDescripcionReal()).isEqualTo("Nueva descripcion");
    }

    @Test
    void debeDejarDescripcionVacia_cuandoNulaOEnBlanco() {
        // Act
        var sinDescripcion = ModificacionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), 1, null);
        var enBlanco = ModificacionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), 1, "   ");

        // Assert
        assertThat(sinDescripcion.getDescripcionReal()).isEqualTo(UtilTexto.VACIO);
        assertThat(enBlanco.getDescripcionReal()).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoPlanTiempoYDescripcionInvalidos() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionPlanRealDomain.crear(null, null, larga))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
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
    void debeFallar_cuandoTiempoMenorAlMinimo() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var bajoElMinimo = MapasRutaLimits.PlanReal.TIEMPO_REAL_DIAS_MIN - 1;

        // Act & Assert
        assertThatThrownBy(() -> ModificacionPlanRealDomain.crear(plan, bajoElMinimo, "x"))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(
                                        tuple(MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_INVALIDO)));
    }
}
