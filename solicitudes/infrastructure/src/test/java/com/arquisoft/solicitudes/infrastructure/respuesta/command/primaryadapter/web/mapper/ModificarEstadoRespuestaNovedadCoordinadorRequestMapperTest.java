package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaNovedadCoordinadorRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarEstadoRespuestaNovedadCoordinadorRequestMapperTest {

    @Test
    void debeArmarElComandoConLosTresDatos_cuandoMapeaElRequest() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var dto = new ModificarEstadoRespuestaNovedadCoordinadorRequestDTO("APROBADA");

        // Act
        var command = ModificarEstadoRespuestaNovedadCoordinadorRequestMapper.toCommand(
                dto, solicitud.toString(), coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }
}
