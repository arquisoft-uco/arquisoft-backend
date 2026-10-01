package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarEstadoRespuestaNovedadCoordinadorMapperTest {

    @Test
    void debeCopiarSolicitudCoordinadorYNuevoEstado_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var command = ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                solicitud.toString(), "APROBADA", coordinador);

        // Act
        var dominio = ModificarEstadoRespuestaNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getCoordinadorUsuario()).isEqualTo(coordinador);
        assertThat(dominio.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
