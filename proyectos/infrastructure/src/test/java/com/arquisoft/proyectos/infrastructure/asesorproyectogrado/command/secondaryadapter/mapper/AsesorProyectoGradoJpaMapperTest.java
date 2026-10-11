package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoJpaMapperTest {

    @Test
    void debeConservarTodosLosCampos_cuandoSeHaceElViajeEntityJpaEntityEntity() {
        // Arrange
        var entity = new AsesorProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "ASIGNADO", UtilFecha.generarInstanteActual());

        // Act
        var jpaEntity = AsesorProyectoGradoJpaMapper.toJpaEntity(entity);
        var devuelto = AsesorProyectoGradoJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getAsesorId()).isEqualTo(entity.asesor());
        assertThat(jpaEntity.getProyectoGradoId()).isEqualTo(entity.proyectoGrado());
        assertThat(devuelto).isEqualTo(entity);
    }
}
