package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutorDocumentacionResponseMapperTest {

    @Test
    void debeCopiarLosCuatroCampos_cuandoMapeaElReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new AutorDocumentacionReadModel(id, "20161020123", "Ana Perez", "ana@uco.edu.co");

        // Act
        var dto = AutorDocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.identificador()).isEqualTo("20161020123");
        assertThat(dto.nombre()).isEqualTo("Ana Perez");
        assertThat(dto.email()).isEqualTo("ana@uco.edu.co");
    }
}
