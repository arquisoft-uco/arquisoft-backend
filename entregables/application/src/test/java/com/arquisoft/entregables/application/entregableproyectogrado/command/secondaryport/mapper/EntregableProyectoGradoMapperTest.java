package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoMapperTest {

    @Test
    void debeMapearCamposYEstadoComoId_cuandoSeConvierteAEntity() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = EntregableProyectoGradoDomain.crear(proyecto, 1, asesor);

        // Act
        var entity = EntregableProyectoGradoMapper.toEntity(entregable);

        // Assert
        assertThat(entity.id()).isEqualTo(entregable.getId());
        assertThat(entity.proyectoGrado()).isEqualTo(proyecto);
        assertThat(entity.version()).isEqualTo(2);
        assertThat(entity.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeReconstruirElDomain_cuandoSeConvierteDesdeEntity() {
        // Arrange
        var entity = new EntregableProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                4, UtilUUID.generarNuevoUUID());

        // Act
        var domain = EntregableProyectoGradoMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getProyectoGrado()).isEqualTo(entity.proyectoGrado());
        assertThat(domain.getVersion()).isEqualTo(4);
        assertThat(domain.getAsesor()).isEqualTo(entity.asesor());
    }
}
