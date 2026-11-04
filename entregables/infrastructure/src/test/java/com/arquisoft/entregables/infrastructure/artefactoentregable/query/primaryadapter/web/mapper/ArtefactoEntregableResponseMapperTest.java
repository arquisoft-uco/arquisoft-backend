package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoEntregableResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos() {
        // Arrange
        var rm = new ArtefactoEntregableReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "uri", UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Doc", UtilUUID.generarNuevoUUID(), "Repo");

        // Act
        var dto = ArtefactoEntregableResponseMapper.toResponse(rm);

        // Assert
        assertThat(dto.id()).isEqualTo(rm.id());
        assertThat(dto.entregableProyectoGrado()).isEqualTo(rm.entregableProyectoGrado());
        assertThat(dto.versionArtefacto()).isEqualTo(rm.versionArtefacto());
        assertThat(dto.uriArchivo()).isEqualTo("uri");
        assertThat(dto.artefacto()).isEqualTo(rm.artefacto());
        assertThat(dto.versionRepositorioArtefacto()).isEqualTo(rm.versionRepositorioArtefacto());
        assertThat(dto.nombreDocumento()).isEqualTo("Doc");
        assertThat(dto.repositorio()).isEqualTo(rm.repositorio());
        assertThat(dto.nombreRepositorio()).isEqualTo("Repo");
    }
}
