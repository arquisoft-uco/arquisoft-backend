package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new EstudianteProyectoGradoReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "2020", "Ana", "ana@soyuco.edu.co", false);

        // Act
        var dto = EstudianteProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.proyectoGradoId()).isEqualTo(readModel.proyectoGradoId());
        assertThat(dto.estudianteId()).isEqualTo(readModel.estudianteId());
        assertThat(dto.identificador()).isEqualTo("2020");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@soyuco.edu.co");
        assertThat(dto.vigente()).isFalse();
    }
}
