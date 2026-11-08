package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.AsesorActualProyectoGradoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorActualProyectoGradoQueryMapperTest {

    @Test
    void debeProyectarLosSeisCampos_cuandoHayEntidad() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entity = AsesorActualProyectoGradoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(proyecto)
                .tituloProyecto("Titulo")
                .asesorId(asesor)
                .identificador("1001")
                .nombre("Ana")
                .email("ana@soyuco.edu.co")
                .build();

        // Act
        var readModel = AsesorActualProyectoGradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.proyectoGradoId()).isEqualTo(proyecto);
        assertThat(readModel.tituloProyecto()).isEqualTo("Titulo");
        assertThat(readModel.asesorId()).isEqualTo(asesor);
        assertThat(readModel.identificador()).isEqualTo("1001");
        assertThat(readModel.nombre()).isEqualTo("Ana");
        assertThat(readModel.email()).isEqualTo("ana@soyuco.edu.co");
    }
}
