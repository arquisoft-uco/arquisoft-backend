package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadAsesorDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudNovedadAsesorMapperTest {

    @Test
    void debeMapearLosTresCampos_cuandoConvierteElComando() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID asesor = UUID.randomUUID();
        var command = new ResponderSolicitudNovedadAsesorCommand(solicitud, "contenido", asesor);

        // Act
        RespuestaNovedadAsesorDomain accion =
                ResponderSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getContenido()).isEqualTo("contenido");
        assertThat(accion.getAsesorUsuario()).isEqualTo(asesor);
    }
}
