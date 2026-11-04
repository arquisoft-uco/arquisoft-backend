package com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoMapperTest {

    @Test
    void debeMapearAEntity_cuandoDomainValido() {
        // Arrange
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 6, "Revisar");

        // Act
        var entity = PlanEstimadoMapper.toEntity(plan);

        // Assert
        assertThat(entity.id()).isEqualTo(plan.getId());
        assertThat(entity.tarea()).isEqualTo(plan.getTarea());
        assertThat(entity.tiempoEstimadoDias()).isEqualTo(6);
        assertThat(entity.descripcionEstimado()).isEqualTo("Revisar");
    }

    @Test
    void debeMapearDescripcionVacia_cuandoNoSeEnvio() {
        // Arrange
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 6, null);

        // Act
        var entity = PlanEstimadoMapper.toEntity(plan);

        // Assert
        assertThat(entity.descripcionEstimado()).isEqualTo(UtilTexto.VACIO);
    }
}
