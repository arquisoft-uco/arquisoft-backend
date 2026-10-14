package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteAsesoradoProyectoGradoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new EstudianteAsesoradoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2020", "Ana",
                "ana@soyuco.edu.co");

        // Act
        var dto = EstudianteAsesoradoProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.estudianteId()).isEqualTo(readModel.estudianteId());
        assertThat(dto.identificador()).isEqualTo("2020");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@soyuco.edu.co");
    }
}
