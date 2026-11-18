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
        assertThat(entity.creadoEn()).isEqualTo(plan.getCreadoEn());
    }

    @Test
    void debeMapearADomain_cuandoEntityValida() {
        // Arrange
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 6, "Revisar");
        var entity = PlanEstimadoMapper.toEntity(plan);

        // Act
        var reconstruido = PlanEstimadoMapper.toDomain(entity);

        // Assert
        assertThat(reconstruido.getId()).isEqualTo(plan.getId());
        assertThat(reconstruido.getTarea()).isEqualTo(plan.getTarea());
        assertThat(reconstruido.getTiempoEstimadoDias()).isEqualTo(6);
        assertThat(reconstruido.getDescripcionEstimado()).isEqualTo("Revisar");
        assertThat(reconstruido.getCreadoEn()).isEqualTo(plan.getCreadoEn());
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
