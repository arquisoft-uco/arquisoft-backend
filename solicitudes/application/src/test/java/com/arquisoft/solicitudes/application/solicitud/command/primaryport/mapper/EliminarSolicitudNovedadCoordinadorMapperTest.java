package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarSolicitudNovedadCoordinadorMapperTest {

    @Test
    void debeCopiarSolicitudYRemitente_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var command = EliminarSolicitudNovedadCoordinadorCommand.crear(
                solicitud.toString(), remitente);

        // Act
        var dominio =
                EliminarSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getRemitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeFijarElTipoEsperadoDelFlujo_cuandoMapeaElComando() {
        // Arrange
        var command = EliminarSolicitudNovedadCoordinadorCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID());

        // Act
        var dominio = EliminarSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }
}
