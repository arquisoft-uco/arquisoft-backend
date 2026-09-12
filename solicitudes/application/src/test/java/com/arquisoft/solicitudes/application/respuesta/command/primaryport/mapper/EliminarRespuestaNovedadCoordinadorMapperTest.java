package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRespuestaNovedadCoordinadorMapperTest {

    @Test
    void debeCopiarSolicitudYCoordinadorUsuario_cuandoMapeaElComando() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();
        var command = EliminarRespuestaNovedadCoordinadorCommand.crear(
                solicitud.toString(), coordinador.toString());

        // Act
        EliminacionRespuestaNovedadCoordinadorDomain dominio =
                EliminarRespuestaNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getCoordinadorUsuario()).isEqualTo(coordinador);
    }
}
