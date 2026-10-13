package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorioArtefactoJpaMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteLaEntidadAJpa() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var entity = new RepositorioArtefactoEntity(id, "Repo", "Descripcion", administrador);

        // Act
        var jpa = RepositorioArtefactoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(id);
        assertThat(jpa.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(jpa.getDescripcion()).isEqualTo("Descripcion");
        assertThat(jpa.getAdministrador()).isEqualTo(administrador);
    }
}
