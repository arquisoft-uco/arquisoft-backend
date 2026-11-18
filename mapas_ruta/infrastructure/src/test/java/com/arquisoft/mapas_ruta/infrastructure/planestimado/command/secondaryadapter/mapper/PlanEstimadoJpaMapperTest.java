package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.entity.PlanEstimadoJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity() {
        // Arrange
        var entity = new PlanEstimadoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 5, "Revisar", Instant.now());

        // Act
        var jpa = PlanEstimadoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getTareaId()).isEqualTo(entity.tarea());
        assertThat(jpa.getTiempoEstimadoDias()).isEqualTo(5);
        assertThat(jpa.getDescripcionEstimado()).isEqualTo("Revisar");
        assertThat(jpa.getCreadoEn()).isEqualTo(entity.creadoEn());
    }

    @Test
    void debeMapearJpaEntityAEntity_conCreadoEnYDescripcionVaciaSiEsNull() {
        // Arrange
        var creadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var jpa = PlanEstimadoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tareaId(UtilUUID.generarNuevoUUID())
                .tiempoEstimadoDias(4)
                .creadoEn(creadoEn)
                .build();

        // Act
        var entity = PlanEstimadoJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity.id()).isEqualTo(jpa.getId());
        assertThat(entity.tarea()).isEqualTo(jpa.getTareaId());
        assertThat(entity.tiempoEstimadoDias()).isEqualTo(4);
        assertThat(entity.descripcionEstimado()).isEqualTo(UtilTexto.VACIO);
        assertThat(entity.creadoEn()).isEqualTo(creadoEn);
    }

    @Test
    void debeMapearDescripcionVaciaANull() {
        // Arrange
        var entity = new PlanEstimadoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 5, UtilTexto.VACIO, Instant.now());

        // Act
        var jpa = PlanEstimadoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getDescripcionEstimado()).isNull();
    }
}
