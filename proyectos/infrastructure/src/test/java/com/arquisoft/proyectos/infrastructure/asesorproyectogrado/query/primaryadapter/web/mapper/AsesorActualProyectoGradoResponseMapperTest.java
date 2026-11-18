package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorActualProyectoGradoResponseMapperTest {

    @Test
    void debeMapearLosSeisCampos_cuandoHayReadModel() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new AsesorActualProyectoGradoReadModel(proyecto, "Titulo", asesor, "1001", "Ana",
                "ana@soyuco.edu.co");

        // Act
        var dto = AsesorActualProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.proyectoGradoId()).isEqualTo(proyecto);
        assertThat(dto.tituloProyecto()).isEqualTo("Titulo");
        assertThat(dto.asesorId()).isEqualTo(asesor);
        assertThat(dto.identificador()).isEqualTo("1001");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@soyuco.edu.co");
    }
}
