package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudNovedadCoordinadorMapperTest {

    @Test
    void debeMapearLosTresCampos_cuandoConvierteElComando() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();
        var command = new ResponderSolicitudNovedadCoordinadorCommand(solicitud, "contenido", coordinador);

        // Act
        RespuestaNovedadCoordinadorDomain accion =
                ResponderSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getContenido()).isEqualTo("contenido");
        assertThat(accion.getCoordinadorUsuario()).isEqualTo(coordinador);
    }
}
