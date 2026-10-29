package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper;

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
}
