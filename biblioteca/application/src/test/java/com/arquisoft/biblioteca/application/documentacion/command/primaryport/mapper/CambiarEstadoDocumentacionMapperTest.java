package com.arquisoft.biblioteca.application.documentacion.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.CambiarEstadoDocumentacionCommand;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoDocumentacionMapperTest {

    @Test
    void debeMapearAObjetoDeAccion_cuandoElCommandEsValido() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var command = CambiarEstadoDocumentacionCommand.crear(documentacion, "NO_PUBLICADO");

        // Act
        var cambio = CambiarEstadoDocumentacionMapper.toDomain(command);

        // Assert
        assertThat(cambio.getDocumentacion()).isEqualTo(documentacion);
        assertThat(cambio.getEstado()).isEqualTo(EstadoDocumentacion.NO_PUBLICADO);
    }
}
