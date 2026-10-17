package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
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

    @Test
    void debeFijarElTipoEsperadoDelFlujo_cuandoMapeaElComando() {
        // Arrange
        var command = EliminarSolicitudNovedadAsesorCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID());

        // Act
        var dominio = EliminarSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
    }
}
