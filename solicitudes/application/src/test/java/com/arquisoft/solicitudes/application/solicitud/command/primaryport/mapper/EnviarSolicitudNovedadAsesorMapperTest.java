package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudNovedadAsesorMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        var remitente = UUID.randomUUID();
        var destinatario = UUID.randomUUID();
        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                remitente, destinatario.toString(), "  novedad  ");

        // Act
        var envio =
                EnviarSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("novedad");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
        assertThat(envio.getSolicitud().getRemitenteUsuario()).isEqualTo(envio.getRemitente().getUsuario());
        assertThat(envio.getSolicitud().getDestinatarioUsuario()).isEqualTo(envio.getDestinatario().getUsuario());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                UUID.randomUUID(), UUID.randomUUID().toString(), "mensaje");

        // Act
        var envio =
                EnviarSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
