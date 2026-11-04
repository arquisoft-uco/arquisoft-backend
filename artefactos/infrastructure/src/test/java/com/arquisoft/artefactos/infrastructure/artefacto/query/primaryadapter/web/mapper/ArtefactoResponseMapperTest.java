package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoResponseMapperTest {

    @Test
    void debeMapearCampoACampo_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new ArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Proyecto Alfa",
                UtilUUID.generarNuevoUUID(), "Documento", 3, "Repositorio", true);

        // Act
        var response = ArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(readModel.id());
        assertThat(response.proyectoGrado()).isEqualTo(readModel.proyectoGrado());
        assertThat(response.nombreProyecto()).isEqualTo("Proyecto Alfa");
        assertThat(response.versionRepositorioArtefacto()).isEqualTo(readModel.versionRepositorioArtefacto());
        assertThat(response.nombreDocumento()).isEqualTo("Documento");
        assertThat(response.versionDocumento()).isEqualTo(3);
        assertThat(response.nombreRepositorio()).isEqualTo("Repositorio");
        assertThat(response.omitido()).isTrue();
    }
}
