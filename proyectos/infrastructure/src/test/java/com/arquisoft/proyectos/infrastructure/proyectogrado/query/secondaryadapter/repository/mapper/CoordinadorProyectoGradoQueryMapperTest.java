package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.CoordinadorProyectoGradoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorProyectoGradoQueryMapperTest {

    @Test
    void debeProyectarLosSeisCampos_cuandoHayEntidad() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var entity = CoordinadorProyectoGradoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(proyecto)
                .tituloProyecto("Titulo")
                .coordinadorId(coordinador)
                .identificador("1001")
                .nombre("Ana")
                .email("ana@uco.edu.co")
                .build();

        // Act
        var readModel = CoordinadorProyectoGradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.proyectoGradoId()).isEqualTo(proyecto);
        assertThat(readModel.tituloProyecto()).isEqualTo("Titulo");
        assertThat(readModel.coordinadorId()).isEqualTo(coordinador);
        assertThat(readModel.identificador()).isEqualTo("1001");
        assertThat(readModel.nombre()).isEqualTo("Ana");
        assertThat(readModel.email()).isEqualTo("ana@uco.edu.co");
    }
}
