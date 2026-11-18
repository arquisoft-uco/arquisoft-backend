package com.arquisoft.mapas_ruta.application.planreal.command.result.mapper;

import com.arquisoft.mapas_ruta.domain.planreal.GeneracionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.PlanRealDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class GeneracionPlanRealResultMapperTest {

    @Test
    void debeMapearGeneradaYRecalculada_conTareaYDias() {
        // Arrange
        var inicio = Instant.parse("2026-11-01T10:00:00Z");
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), inicio, inicio.plusSeconds(90000), null);

        // Act
        var generada = GeneracionPlanRealResultMapper.toResultGenerada(plan);
        var recalculada = GeneracionPlanRealResultMapper.toResultRecalculada(plan);

        // Assert
        assertThat(generada.tarea()).isEqualTo(plan.getTarea());
        assertThat(generada.tiempoRealDias()).isEqualTo(2);
        assertThat(recalculada.tarea()).isEqualTo(plan.getTarea());
        assertThat(recalculada.tiempoRealDias()).isEqualTo(2);
    }

    @Test
    void debeMapearDescartada_conTarea() {
        // Arrange
        var generacion = GeneracionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), Instant.now());

        // Act
        var descartada = GeneracionPlanRealResultMapper.toResultDescartada(generacion);

        // Assert
        assertThat(descartada.tarea()).isEqualTo(generacion.getTarea());
    }
}
