package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.ProyectoGradoEstudianteJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoQueryMapperTest {

    @Test
    void debeMapearIdDelProyecto_cuandoEntidadDeEstudiante() {
        // Arrange
        var vinculo = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        var entity = ProyectoGradoEstudianteJpaQueryEntity.builder()
                .id(vinculo)
                .proyectoGradoId(proyecto)
                .estudianteId(UtilUUID.generarNuevoUUID())
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
