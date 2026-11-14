package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity() {
        // Arrange
        var entity = new PlanRealEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 3, "Hecho");

        // Act
        var jpa = PlanRealJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getTareaId()).isEqualTo(entity.tarea());
        assertThat(jpa.getTiempoRealDias()).isEqualTo(3);
        assertThat(jpa.getDescripcionReal()).isEqualTo("Hecho");
    }

    @Test
    void debeMapearDescripcionVaciaANull() {
        // Arrange
        var entity = new PlanRealEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 3, UtilTexto.VACIO);

        // Act
        var jpa = PlanRealJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getDescripcionReal()).isNull();
    }
}
