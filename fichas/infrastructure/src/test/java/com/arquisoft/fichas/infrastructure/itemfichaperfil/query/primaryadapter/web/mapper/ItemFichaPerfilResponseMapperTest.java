package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.dto.ItemFichaPerfilResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemFichaPerfilResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos() {
        // Arrange
        var id = UUID.randomUUID();
        var fichaPerfilId = UUID.randomUUID();
        var readModel = new ItemFichaPerfilReadModel(
                id, fichaPerfilId, "OBJETIVO_GENERAL", "Objetivo General", "Contenido del item");

        // Act
        ItemFichaPerfilResponseDTO dto = ItemFichaPerfilResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.fichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(dto.tipoItem()).isEqualTo("OBJETIVO_GENERAL");
        assertThat(dto.tipoItemNombre()).isEqualTo("Objetivo General");
        assertThat(dto.contenido()).isEqualTo("Contenido del item");
    }
}
