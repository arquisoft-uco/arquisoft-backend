package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorioArtefactoResponseMapperTest {

    @Test
    void debeMapearElAdministradorAnidado_cuandoConvierteElReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var readModel = new RepositorioArtefactoReadModel(id, "Repo", "Descripcion",
                new AdministradorReadModel(administrador, "123", "Ana Perez", "ana@uco.edu.co"));

        // Act
        var dto = RepositorioArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nombreRepositorio()).isEqualTo("Repo");
        assertThat(dto.descripcion()).isEqualTo("Descripcion");
        assertThat(dto.administrador().id()).isEqualTo(administrador);
        assertThat(dto.administrador().identificador()).isEqualTo("123");
        assertThat(dto.administrador().nombre()).isEqualTo("Ana Perez");
        assertThat(dto.administrador().email()).isEqualTo("ana@uco.edu.co");
    }
}
