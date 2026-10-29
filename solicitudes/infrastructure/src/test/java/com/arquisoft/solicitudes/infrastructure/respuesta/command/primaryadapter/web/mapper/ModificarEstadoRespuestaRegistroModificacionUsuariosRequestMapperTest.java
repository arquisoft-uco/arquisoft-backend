package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaRegistroModificacionUsuariosRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarEstadoRespuestaRegistroModificacionUsuariosRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var dto = new ModificarEstadoRespuestaRegistroModificacionUsuariosRequestDTO("APROBADA");

        // Act
        var command = ModificarEstadoRespuestaRegistroModificacionUsuariosRequestMapper.toCommand(
                dto, solicitud, administrador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.administradorUsuario()).isEqualTo(administrador);
    }
}
