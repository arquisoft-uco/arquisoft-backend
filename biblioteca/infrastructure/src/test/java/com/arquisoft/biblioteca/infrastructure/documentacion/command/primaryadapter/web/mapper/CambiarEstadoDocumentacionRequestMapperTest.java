package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.dto.CambiarEstadoDocumentacionRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoDocumentacionRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoRecibeElIdDeRutaYElDto() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var dto = new CambiarEstadoDocumentacionRequestDTO("NO_PUBLICADO");

        // Act
        var command = CambiarEstadoDocumentacionRequestMapper.toCommand(documentacion, dto);

        // Assert
        assertThat(command.documentacion()).isEqualTo(documentacion);
        assertThat(command.estado()).isEqualTo("NO_PUBLICADO");
    }
}
