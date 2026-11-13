package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.EnlaceDocumentacionResponseDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnlaceDocumentacionResponseMapperTest {

    @Test
    void debeMapearTodosLosCamposSinUrlNiFechaPublicacion_cuandoRecibeElReadModel() {
        // Arrange
        var readModel = new EnlaceDocumentacionReadModel(
                "https://front.test/doc/x", UtilUUID.generarNuevoUUID(),
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado"),
                new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana"),
                new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado"));

        // Act
        var dto = EnlaceDocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.enlace()).isEqualTo("https://front.test/doc/x");
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.entregableProyectoGrado().id()).isEqualTo(readModel.entregableProyectoGrado().id());
        assertThat(dto.entregableProyectoGrado().proyectoGrado())
                .isEqualTo(readModel.entregableProyectoGrado().proyectoGrado());
        assertThat(dto.entregableProyectoGrado().versionEntregable()).isEqualTo(2);
        assertThat(dto.entregableProyectoGrado().estadoEntregable()).isEqualTo("Aprobado");
        assertThat(dto.bibliotecario().id()).isEqualTo(readModel.bibliotecario().id());
        assertThat(dto.bibliotecario().nombre()).isEqualTo("Ana");
        assertThat(dto.estadoDocumentacion().id()).isEqualTo("PUBLICADO");
        assertThat(dto.estadoDocumentacion().nombre()).isEqualTo("Publicado");
        assertThat(EnlaceDocumentacionResponseDTO.class.getRecordComponents())
                .extracting("name").doesNotContain("url", "fechaPublicacion");
    }
}
