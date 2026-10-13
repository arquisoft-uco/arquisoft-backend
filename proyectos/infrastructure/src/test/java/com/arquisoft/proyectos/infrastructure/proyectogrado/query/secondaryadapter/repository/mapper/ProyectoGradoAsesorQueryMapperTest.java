package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.ProyectoGradoAsesorJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoAsesorQueryMapperTest {

    @Test
    void debeMapearIdDelProyecto_cuandoEntidadDeAsesor() {
        // Arrange
        var asignacion = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        var entity = ProyectoGradoAsesorJpaQueryEntity.builder()
                .id(asignacion)
                .proyectoGradoId(proyecto)
                .asesorId(UtilUUID.generarNuevoUUID())
                .estadoAsesorId("ASIGNADO")
                .fichaPerfilId(ficha)
                .tituloProyecto("Titulo")
                .estadoId("EN_PROCESO")
                .estadoNombre("En proceso")
                .build();

        // Act
        var readModel = ProyectoGradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(proyecto);
        assertThat(readModel.fichaPerfil()).isEqualTo(ficha);
        assertThat(readModel.tituloProyecto()).isEqualTo("Titulo");
        assertThat(readModel.estado().id()).isEqualTo("EN_PROCESO");
        assertThat(readModel.estado().nombre()).isEqualTo("En proceso");
    }
}
