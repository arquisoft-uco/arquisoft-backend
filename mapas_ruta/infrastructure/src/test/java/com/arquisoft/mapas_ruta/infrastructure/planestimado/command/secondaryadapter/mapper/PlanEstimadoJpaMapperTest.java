package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity() {
        // Arrange
        var entity = new PlanEstimadoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 5, "Revisar");

        // Act
        var jpa = PlanEstimadoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getTareaId()).isEqualTo(entity.tarea());
        assertThat(jpa.getTiempoEstimadoDias()).isEqualTo(5);
        assertThat(jpa.getDescripcionEstimado()).isEqualTo("Revisar");
    }

    @Test
    void debeMapearDescripcionVaciaANull() {
        // Arrange
        var entity = new PlanEstimadoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 5, UtilTexto.VACIO);

        // Act
        var jpa = PlanEstimadoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getDescripcionEstimado()).isNull();
    }
}
