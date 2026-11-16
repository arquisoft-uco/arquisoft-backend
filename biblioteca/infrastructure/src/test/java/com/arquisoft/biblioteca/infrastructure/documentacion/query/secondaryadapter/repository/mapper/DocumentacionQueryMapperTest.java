package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.AutorDeDocumentacionJpaQueryEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.DocumentacionJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionQueryMapperTest {

    @Test
    void debeMapearTodosLosCamposYResumenes_cuandoRecibeLaEntidad() {
        // Arrange
        var fecha = Instant.parse("2026-11-10T10:00:00Z");
        var entity = DocumentacionJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).tituloProyecto("Proyecto").fechaPublicacion(fecha)
                .entregableId(UtilUUID.generarNuevoUUID()).proyectoGradoId(UtilUUID.generarNuevoUUID())
                .entregableVersion(4L).entregableEstado("Aprobado")
                .bibliotecarioId(UtilUUID.generarNuevoUUID()).bibliotecarioNombre("Ana")
                .estadoId("PUBLICADO").estadoNombre("Publicado").build();

        // Act
        var readModel = DocumentacionQueryMapper.toReadModel(entity, List.of());

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.tituloProyecto()).isEqualTo("Proyecto");
        assertThat(readModel.autores()).isEmpty();
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

    @Test
    void debeMapearTituloYAutores_cuandoRecibeLaEntidadYSusAutores() {
        // Arrange
        var entity = DocumentacionJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).tituloProyecto(null)
                .entregableId(UtilUUID.generarNuevoUUID()).proyectoGradoId(UtilUUID.generarNuevoUUID())
                .entregableVersion(1L).entregableEstado("Aprobado")
                .bibliotecarioId(UtilUUID.generarNuevoUUID()).bibliotecarioNombre("Ana")
                .estadoId("PUBLICADO").estadoNombre("Publicado").build();
        var autorUno = AutorDeDocumentacionJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).documentacionId(entity.getId())
                .autorId(UtilUUID.generarNuevoUUID()).identificador("AUT-1").nombre("Beto").build();
        var autorDos = AutorDeDocumentacionJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).documentacionId(entity.getId())
                .autorId(UtilUUID.generarNuevoUUID()).identificador("AUT-2").nombre("Carlos").build();

        // Act
        var readModel = DocumentacionQueryMapper.toReadModel(entity, List.of(autorUno, autorDos));

        // Assert
        assertThat(readModel.tituloProyecto()).isNull();
        assertThat(readModel.autores()).hasSize(2);
        assertThat(readModel.autores().get(0).id()).isEqualTo(autorUno.getAutorId());
        assertThat(readModel.autores().get(0).identificador()).isEqualTo("AUT-1");
        assertThat(readModel.autores().get(1).nombre()).isEqualTo("Carlos");
    }
}
