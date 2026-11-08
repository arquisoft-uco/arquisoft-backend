package com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.domain.planreal.PlanRealDomain;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealMapperTest {

    @Test
    void debeMapearAEntity_cuandoDomainValido() {
        // Arrange
        var inicio = Instant.parse("2026-11-01T10:00:00Z");
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), inicio, inicio.plusSeconds(90000), "Hecho");

        // Act
        var entity = PlanRealMapper.toEntity(plan);

        // Assert
        assertThat(entity.id()).isEqualTo(plan.getId());
        assertThat(entity.tarea()).isEqualTo(plan.getTarea());
        assertThat(entity.tiempoRealDias()).isEqualTo(2);
        assertThat(entity.descripcionReal()).isEqualTo("Hecho");
    }

    @Test
    void debeMapearDescripcionVacia_cuandoNoHayDescripcion() {
        // Arrange
        var inicio = Instant.parse("2026-11-01T10:00:00Z");
        var plan = PlanRealDomain.crear(UtilUUID.generarNuevoUUID(), inicio, inicio, null);

        // Act
        var entity = PlanRealMapper.toEntity(plan);

        // Assert
        assertThat(entity.descripcionReal()).isEqualTo(UtilTexto.VACIO);
    }
}
