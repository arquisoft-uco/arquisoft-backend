package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoDocumentacionResponseMapperTest {

    @Test
    void debeCopiarLosTresCampos_cuandoMapeaElReadModel() {
        // Arrange
        var readModel = new EstadoDocumentacionReadModel("PUBLICADO", "Publicado", "Publicada");

        // Act
        var dto = EstadoDocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo("PUBLICADO");
        assertThat(dto.nombre()).isEqualTo("Publicado");
        assertThat(dto.descripcion()).isEqualTo("Publicada");
    }
}
