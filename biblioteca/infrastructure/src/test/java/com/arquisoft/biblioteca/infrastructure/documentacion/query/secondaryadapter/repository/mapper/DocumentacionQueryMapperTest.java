package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.DocumentacionJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionQueryMapperTest {

    @Test
    void debeMapearTodosLosCamposYResumenes_cuandoRecibeLaEntidad() {
        // Arrange
        var fecha = Instant.parse("2026-11-10T10:00:00Z");
        var entity = DocumentacionJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).url("https://biblioteca.test/doc").fechaPublicacion(fecha)
                .entregableId(UtilUUID.generarNuevoUUID()).proyectoGradoId(UtilUUID.generarNuevoUUID())
                .entregableVersion(4L).entregableEstado("Aprobado")
                .bibliotecarioId(UtilUUID.generarNuevoUUID()).bibliotecarioNombre("Ana")
                .estadoId("PUBLICADO").estadoNombre("Publicado").build();

        // Act
        var readModel = DocumentacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.url()).isEqualTo("https://biblioteca.test/doc");
        assertThat(readModel.fechaPublicacion()).isEqualTo(fecha);
        assertThat(readModel.entregableProyectoGrado().id()).isEqualTo(entity.getEntregableId());
        assertThat(readModel.entregableProyectoGrado().proyectoGrado()).isEqualTo(entity.getProyectoGradoId());
        assertThat(readModel.entregableProyectoGrado().versionEntregable()).isEqualTo(4);
        assertThat(readModel.entregableProyectoGrado().estadoEntregable()).isEqualTo("Aprobado");
        assertThat(readModel.bibliotecario().id()).isEqualTo(entity.getBibliotecarioId());
        assertThat(readModel.bibliotecario().nombre()).isEqualTo("Ana");
        assertThat(readModel.estadoDocumentacion().id()).isEqualTo("PUBLICADO");
        assertThat(readModel.estadoDocumentacion().nombre()).isEqualTo("Publicado");
    }
}
