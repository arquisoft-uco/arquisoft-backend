package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.ArtefactoEntregableJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoEntregableQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos() {
        // Arrange
        var entity = ArtefactoEntregableJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).entregableId(UtilUUID.generarNuevoUUID())
                .artefactoId(UtilUUID.generarNuevoUUID()).uriArchivo("uri")
                .idArtefacto(UtilUUID.generarNuevoUUID()).idRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .nombreDocumento("Doc").idRepositorio(UtilUUID.generarNuevoUUID())
                .nombreRepositorio("Repo").build();

        // Act
        var readModel = ArtefactoEntregableQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.entregableProyectoGrado()).isEqualTo(entity.getEntregableId());
        assertThat(readModel.versionArtefacto()).isEqualTo(entity.getArtefactoId());
        assertThat(readModel.uriArchivo()).isEqualTo("uri");
        assertThat(readModel.artefacto()).isEqualTo(entity.getIdArtefacto());
        assertThat(readModel.versionRepositorioArtefacto()).isEqualTo(entity.getIdRepositorioArtefacto());
        assertThat(readModel.nombreDocumento()).isEqualTo("Doc");
        assertThat(readModel.repositorio()).isEqualTo(entity.getIdRepositorio());
        assertThat(readModel.nombreRepositorio()).isEqualTo("Repo");
    }
}
