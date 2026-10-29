package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudRegistroModificacionUsuariosRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudRegistroModificacionUsuariosRequestMapperTest {

    @Test
    void debeArmarElComandoConLosTresDatos_cuandoMapeaElRequest() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var dto = new ResponderSolicitudRegistroModificacionUsuariosRequestDTO("Se aprueba el registro");

        // Act
        var command = ResponderSolicitudRegistroModificacionUsuariosRequestMapper.toCommand(
                dto, solicitud, administrador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.contenido()).isEqualTo("Se aprueba el registro");
        assertThat(command.administradorUsuario()).isEqualTo(administrador);
    }
}
