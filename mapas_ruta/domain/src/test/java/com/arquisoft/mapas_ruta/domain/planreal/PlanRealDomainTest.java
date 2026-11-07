package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class PlanRealDomainTest {

    private static final Instant CREADO_EN = Instant.parse("2026-11-01T10:00:00Z");

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var plan = PlanRealDomain.crear(tarea, CREADO_EN, CREADO_EN.plus(Duration.ofDays(3)), "  Se hizo  ");

        // Assert
        assertThat(plan.getId()).isNotNull();
        assertThat(plan.getTarea()).isEqualTo(tarea);
        assertThat(plan.getTiempoRealDias()).isEqualTo(3);
        assertThat(plan.getDescripcionReal()).isEqualTo("Se hizo");
    }

    @Test
    void debeCalcularUnDia_cuandoMenosDe24Horas() {
        // Act
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), CREADO_EN,
                CREADO_EN.plus(Duration.ofHours(5)), null);

        // Assert
        assertThat(plan.getTiempoRealDias()).isEqualTo(1);
    }

    @Test
    void debeCalcularUnDia_cuandoFinalizadaAntesDeCreado() {
        // Act
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), CREADO_EN,
                CREADO_EN.minus(Duration.ofDays(2)), null);

        // Assert
        assertThat(plan.getTiempoRealDias()).isEqualTo(MapasRutaLimits.PlanReal.TIEMPO_REAL_DIAS_MIN);
    }

    @Test
    void debeRedondearHaciaArriba_cuando25Horas() {
        // Act
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), CREADO_EN,
                CREADO_EN.plus(Duration.ofHours(25)), null);

        // Assert
        assertThat(plan.getTiempoRealDias()).isEqualTo(2);
    }

    @Test
    void debeCalcularDos_cuando48HorasExactas() {
        // Act
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), CREADO_EN,
                CREADO_EN.plus(Duration.ofHours(48)), null);

        // Assert
        assertThat(plan.getTiempoRealDias()).isEqualTo(2);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanTareaYFechas() {
        // Act & Assert
        assertThatThrownBy(() -> PlanRealDomain.crear(null, null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.PlanReal.TAREA,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_REQUERIDA),
                                        tuple(MapasRutaFields.PlanReal.CREADO_EN,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_CREADO_EN_REQUERIDO),
                                        tuple(MapasRutaFields.PlanReal.FINALIZADA_EN,
                                                MapasRutaCodes.PlanReal.PLAN_REAL_FINALIZADA_EN_REQUERIDA)));
    }

    @Test
    void debeRechazar_cuandoDescripcionSuperaMaximo() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var larga = "a".repeat(MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> PlanRealDomain.crear(tarea, CREADO_EN, CREADO_EN, larga))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                                        MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA)));
    }

    @Test
    void debeDevolverVacia_cuandoSinDescripcion() {
        // Act
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), CREADO_EN, CREADO_EN, null);

        // Assert
        assertThat(plan.getDescripcionReal()).isEqualTo(UtilTexto.VACIO);
    }
}
