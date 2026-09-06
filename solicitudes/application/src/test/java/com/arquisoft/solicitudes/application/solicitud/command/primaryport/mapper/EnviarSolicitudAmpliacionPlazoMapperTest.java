package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudAmpliacionPlazoDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudAmpliacionPlazoMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        var command = EnviarSolicitudAmpliacionPlazoCommand.crear(
                remitente.toString(), destinatario.toString(), "  ampliacion de plazo  ");

        // Act
        EnvioSolicitudAmpliacionPlazoDomain envio =
                EnviarSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("ampliacion de plazo");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
        assertThat(envio.getSolicitud().getRemitente()).isEqualTo(envio.getRemitente().getId());
        assertThat(envio.getSolicitud().getDestinatario()).isEqualTo(envio.getDestinatario().getId());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudAmpliacionPlazoCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), "mensaje");

        // Act
        EnvioSolicitudAmpliacionPlazoDomain envio =
                EnviarSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
