package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionResponseMapperTest {

    @Test
    void debeMapearTodosLosCamposYResumenes_cuandoRecibeElReadModel() {
        // Arrange
        var fecha = Instant.parse("2026-11-10T10:00:00Z");
        var readModel = new DocumentacionReadModel(
                UtilUUID.generarNuevoUUID(), "Proyecto", fecha,
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado"),
                new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana"),
                new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado"),
                List.of());

        // Act
        var dto = DocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.tituloProyecto()).isEqualTo("Proyecto");
        assertThat(dto.autores()).isEmpty();
        assertThat(dto.fechaPublicacion()).isEqualTo(fecha);
        assertThat(dto.entregableProyectoGrado().id()).isEqualTo(readModel.entregableProyectoGrado().id());
        assertThat(dto.entregableProyectoGrado().proyectoGrado())
                .isEqualTo(readModel.entregableProyectoGrado().proyectoGrado());
        assertThat(dto.entregableProyectoGrado().versionEntregable()).isEqualTo(2);
        assertThat(dto.entregableProyectoGrado().estadoEntregable()).isEqualTo("Aprobado");
        assertThat(dto.bibliotecario().id()).isEqualTo(readModel.bibliotecario().id());
        assertThat(dto.bibliotecario().nombre()).isEqualTo("Ana");
        assertThat(dto.estadoDocumentacion().id()).isEqualTo("PUBLICADO");
        assertThat(dto.estadoDocumentacion().nombre()).isEqualTo("Publicado");
    }

    @Test
    void debeMapearTituloNuloYAutores_cuandoElReadModelTraeAutoresSinTitulo() {
        // Arrange
        var autor = new DocumentacionReadModel.AutorResumen(UtilUUID.generarNuevoUUID(), "AUT-1", "Beto");
        var readModel = new DocumentacionReadModel(
                UtilUUID.generarNuevoUUID(), null, Instant.parse("2026-11-10T10:00:00Z"),
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado"),
                new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana"),
                new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado"),
                List.of(autor));

        // Act
        var dto = DocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.tituloProyecto()).isNull();
        assertThat(dto.autores()).hasSize(1);
        assertThat(dto.autores().get(0).id()).isEqualTo(autor.id());
        assertThat(dto.autores().get(0).identificador()).isEqualTo("AUT-1");
        assertThat(dto.autores().get(0).nombre()).isEqualTo("Beto");
    }
}
