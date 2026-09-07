package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadCoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarSolicitudNovedadCoordinadorMapperTest {

    @Test
    void debeCopiarSolicitudYRemitente_cuandoMapeaElComando() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();
        var command = EliminarSolicitudNovedadCoordinadorCommand.crear(
                solicitud.toString(), remitente.toString());

        // Act
        EliminacionSolicitudNovedadCoordinadorDomain dominio =
                EliminarSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getRemitenteUsuario()).isEqualTo(remitente);
    }
}
