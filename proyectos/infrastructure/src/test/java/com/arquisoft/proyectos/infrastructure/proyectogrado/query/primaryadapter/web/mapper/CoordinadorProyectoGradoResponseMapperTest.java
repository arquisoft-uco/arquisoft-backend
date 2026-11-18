package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorProyectoGradoResponseMapperTest {

    @Test
    void debeMapearLosSeisCampos_cuandoHayReadModel() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var readModel = new CoordinadorProyectoGradoReadModel(proyecto, "Titulo", coordinador, "1001", "Ana",
                "ana@uco.edu.co");

        // Act
        var dto = CoordinadorProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.proyectoGradoId()).isEqualTo(proyecto);
        assertThat(dto.tituloProyecto()).isEqualTo("Titulo");
        assertThat(dto.coordinadorId()).isEqualTo(coordinador);
        assertThat(dto.identificador()).isEqualTo("1001");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@uco.edu.co");
    }
}
