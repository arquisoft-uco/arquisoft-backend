package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudCambioAsesorRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudCambioAsesorRequestMapperTest {

    @Test
    void debeArmarElComandoConLosTresDatos_cuandoMapeaElRequest() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var dto = new ResponderSolicitudCambioAsesorRequestDTO("Se aprueba el cambio");

        // Act
        var command = ResponderSolicitudCambioAsesorRequestMapper.toCommand(
                dto, solicitud.toString(), coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.contenido()).isEqualTo("Se aprueba el cambio");
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }
}
