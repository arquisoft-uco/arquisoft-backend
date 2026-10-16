package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudAmpliacionPlazoMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        var remitente = UUID.randomUUID();
        var destinatario = UUID.randomUUID();
        var command = EnviarSolicitudAmpliacionPlazoCommand.crear(
                remitente, destinatario.toString(), "  ampliacion de plazo  ");

        // Act
        var envio =
                EnviarSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("ampliacion de plazo");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
        assertThat(envio.getSolicitud().getRemitenteUsuario()).isEqualTo(envio.getRemitente().getUsuario());
        assertThat(envio.getSolicitud().getDestinatarioUsuario()).isEqualTo(envio.getDestinatario().getUsuario());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudAmpliacionPlazoCommand.crear(
                UUID.randomUUID(), UUID.randomUUID().toString(), "mensaje");

        // Act
        var envio =
                EnviarSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
