package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionResponseMapperTest {

    @Test
    void debeMapearTodosLosCamposYResumenes_cuandoRecibeElReadModel() {
        // Arrange
        var fecha = Instant.parse("2026-11-10T10:00:00Z");
        var readModel = new DocumentacionReadModel(
                UtilUUID.generarNuevoUUID(), "https://biblioteca.test/doc", fecha,
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado"),
                new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana"),
                new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado"));

        // Act
        var dto = DocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.url()).isEqualTo("https://biblioteca.test/doc");
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
}
