package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudNovedadCoordinadorMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        var command = EnviarSolicitudNovedadCoordinadorCommand.crear(
                remitente.toString(), destinatario.toString(), "  novedad  ");

        // Act
        EnvioSolicitudNovedadCoordinadorDomain envio =
                EnviarSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("novedad");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
        assertThat(envio.getSolicitud().getRemitente()).isEqualTo(envio.getRemitente().getId());
        assertThat(envio.getSolicitud().getDestinatario()).isEqualTo(envio.getDestinatario().getId());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudNovedadCoordinadorCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), "mensaje");

        // Act
        EnvioSolicitudNovedadCoordinadorDomain envio =
                EnviarSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
