package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaNovedadAsesorRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarEstadoRespuestaNovedadAsesorRequestMapperTest {

    @Test
    void debeArmarElComandoConLosTresDatos_cuandoMapeaElRequest() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var dto = new ModificarEstadoRespuestaNovedadAsesorRequestDTO("APROBADA");

        // Act
        var command = ModificarEstadoRespuestaNovedadAsesorRequestMapper.toCommand(
                dto, solicitud.toString(), asesor);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.asesorUsuario()).isEqualTo(asesor);
    }
}
