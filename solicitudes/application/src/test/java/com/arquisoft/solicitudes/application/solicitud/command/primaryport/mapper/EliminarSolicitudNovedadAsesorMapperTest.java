package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadAsesorCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarSolicitudNovedadAsesorMapperTest {

    @Test
    void debeCopiarSolicitudYRemitente_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var command = EliminarSolicitudNovedadAsesorCommand.crear(solicitud.toString(), remitente);

        // Act
        var dominio = EliminarSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getRemitenteUsuario()).isEqualTo(remitente);
    }
}
