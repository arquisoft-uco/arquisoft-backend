package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.dto.EstudianteFichaPerfilResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteFichaPerfilResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conTodosLosCamposYEmailSinEnmascarar() {
        // Arrange
        var id = UUID.randomUUID();
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var readModel = new EstudianteFichaPerfilReadModel(
                id, fichaPerfilId, estudianteId, "Ana Ruiz", "ana.ruiz@uco.edu.co");

        // Act
        EstudianteFichaPerfilResponseDTO dto = EstudianteFichaPerfilResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.fichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(dto.estudianteId()).isEqualTo(estudianteId);
        assertThat(dto.nombre()).isEqualTo("Ana Ruiz");
        assertThat(dto.email()).isEqualTo("ana.ruiz@uco.edu.co");
    }
}
