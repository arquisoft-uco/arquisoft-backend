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

class PlanEstimadoDomainTest {

    @Test
    void debeCrearPlanConId_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var plan = PlanEstimadoDomain.crear(tarea, 5, "Redactar y revisar");

        // Assert
        assertThat(plan.getId()).isNotNull();
        assertThat(plan.getTarea()).isEqualTo(tarea);
        assertThat(plan.getTiempoEstimadoDias()).isEqualTo(5);
        assertThat(plan.getDescripcionEstimado()).isEqualTo("Redactar y revisar");
    }

    @Test
    void debeRecortarDescripcion_cuandoTieneEspacios() {
        // Act
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 1, "  texto  ");

        // Assert
        assertThat(plan.getDescripcionEstimado()).isEqualTo("texto");
    }

    @Test
    void debeDejarDescripcionVacia_cuandoNulaOEnBlanco() {
        // Act
        var sinDescripcion = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 1, null);
        var enBlanco = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 1, "   ");

        // Assert
        assertThat(sinDescripcion.getDescripcionEstimado()).isEqualTo(UtilTexto.VACIO);
        assertThat(enBlanco.getDescripcionEstimado()).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodoEsInvalido() {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> PlanEstimadoDomain.crear(null, null, larga))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
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
    void debeRechazarTiempo_cuandoEsMenorAUno() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> PlanEstimadoDomain.crear(tarea, 0, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                                        MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_INVALIDO)));
    }

    @Test
    void debeAceptarDescripcion_cuandoTieneLongitudMaxima() {
        // Arrange
        var maxima = "a".repeat(MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX);

        // Act
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 2, maxima);

        // Assert
        assertThat(plan.getDescripcionEstimado()).isEqualTo(maxima);
    }
}
