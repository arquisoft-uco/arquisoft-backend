package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.EntregableProyectoGradoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_alReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entity = EntregableProyectoGradoJpaQueryEntity.builder()
                .id(id).versionEntregable(7L)
                .proyectoGradoId(proyecto).proyectoGradoNombre("Proyecto")
                .asesorId(asesor).asesorNombre("Ana").asesorEmail("ana@uco.edu.co")
                .estadoId("APROBADO").estadoNombre("Aprobado")
                .build();

        // Act
        var readModel = EntregableProyectoGradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.versionEntregable()).isEqualTo(7);
        assertThat(readModel.proyectoGrado().id()).isEqualTo(proyecto);
        assertThat(readModel.proyectoGrado().nombre()).isEqualTo("Proyecto");
        assertThat(readModel.asesor().id()).isEqualTo(asesor);
        assertThat(readModel.asesor().nombre()).isEqualTo("Ana");
        assertThat(readModel.asesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(readModel.estadoEntregable().id()).isEqualTo("APROBADO");
        assertThat(readModel.estadoEntregable().nombre()).isEqualTo("Aprobado");
    }
}
