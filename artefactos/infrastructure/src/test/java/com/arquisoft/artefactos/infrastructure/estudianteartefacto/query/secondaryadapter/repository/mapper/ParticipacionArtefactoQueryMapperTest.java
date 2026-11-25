package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.ParticipacionArtefactoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipacionArtefactoQueryMapperTest {

    private static ParticipacionArtefactoJpaQueryEntity.ParticipacionArtefactoJpaQueryEntityBuilder base() {
        return ParticipacionArtefactoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .artefactoId(UtilUUID.generarNuevoUUID())
                .estudianteId(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .nombreProyecto("Proyecto Alfa")
                .versionRepositorioArtefactoId(UtilUUID.generarNuevoUUID())
                .nombreDocumento("Informe")
                .versionDocumento(2)
                .nombreRepositorio("Repo")
                .asesorId(UtilUUID.generarNuevoUUID())
                .nombreAsesor("Ana Asesora");
    }

    @Test
    void debeMapearTodosLosCampos_cuandoEntidadCompleta() {
        // Arrange
        var entity = base()
                .omitido(true)
                .estadoUltimaRevision("Pendiente")
                .proyectoFinalizado(true)
                .proyectoCancelado(false)
                .build();

        // Act
        var readModel = ParticipacionArtefactoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getArtefactoId());
        assertThat(readModel.proyectoGrado()).isEqualTo(entity.getProyectoGradoId());
        assertThat(readModel.nombreProyecto()).isEqualTo("Proyecto Alfa");
        assertThat(readModel.versionRepositorioArtefacto()).isEqualTo(entity.getVersionRepositorioArtefactoId());
        assertThat(readModel.nombreDocumento()).isEqualTo("Informe");
        assertThat(readModel.versionDocumento()).isEqualTo(2);
        assertThat(readModel.nombreRepositorio()).isEqualTo("Repo");
        assertThat(readModel.asesor()).isEqualTo(entity.getAsesorId());
        assertThat(readModel.nombreAsesor()).isEqualTo("Ana Asesora");
        assertThat(readModel.omitido()).isTrue();
        assertThat(readModel.estadoUltimaRevision()).isEqualTo("Pendiente");
        assertThat(readModel.proyectoFinalizado()).isTrue();
        assertThat(readModel.proyectoCancelado()).isFalse();
    }

    @Test
    void debeMapearEstadoNulo_cuandoNoHayRevisiones() {
        // Arrange
        var entity = base().proyectoCancelado(true).build();

        // Act
        var readModel = ParticipacionArtefactoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.estadoUltimaRevision()).isNull();
        assertThat(readModel.proyectoCancelado()).isTrue();
    }
}
