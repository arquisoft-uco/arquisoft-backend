package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoJpaMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoSeConvierteAJpaEntity() {
        // Arrange
        var entity = new EntregableProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                3, UtilUUID.generarNuevoUUID(), "DISPONIBLE_EVALUACION");

        // Act
        var jpa = EntregableProyectoGradoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getProyectoGrado()).isEqualTo(entity.proyectoGrado());
        assertThat(jpa.getVersion()).isEqualTo(3);
        assertThat(jpa.getAsesor()).isEqualTo(entity.asesor());
        assertThat(jpa.getEstadoEntregable()).isEqualTo("DISPONIBLE_EVALUACION");
    }

    @Test
    void debeMapearTodosLosCampos_cuandoSeConvierteAEntity() {
        // Arrange
        var jpa = EntregableProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGrado(UtilUUID.generarNuevoUUID())
                .version(2)
                .asesor(UtilUUID.generarNuevoUUID())
                .estadoEntregable("CERRADO")
                .build();

        // Act
        var entity = EntregableProyectoGradoJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity.id()).isEqualTo(jpa.getId());
        assertThat(entity.proyectoGrado()).isEqualTo(jpa.getProyectoGrado());
        assertThat(entity.version()).isEqualTo(2);
        assertThat(entity.asesor()).isEqualTo(jpa.getAsesor());
        assertThat(entity.estadoEntregable()).isEqualTo("CERRADO");
    }
}
