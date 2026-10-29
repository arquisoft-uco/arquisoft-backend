package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
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
        assertThat(entity.estadoEntregable()).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION.getId());
    }
}
