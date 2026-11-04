package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompaneroProyectoGradoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new CompaneroProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2020", "Ana",
                "ana@soyuco.edu.co");

        // Act
        var dto = CompaneroProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.estudianteId()).isEqualTo(readModel.estudianteId());
        assertThat(dto.identificador()).isEqualTo("2020");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@soyuco.edu.co");
    }
}
