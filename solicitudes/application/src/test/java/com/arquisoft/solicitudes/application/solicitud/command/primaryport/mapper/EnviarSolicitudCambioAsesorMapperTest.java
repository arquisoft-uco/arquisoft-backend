package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudCambioAsesorMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        var command = EnviarSolicitudCambioAsesorCommand.crear(
                remitente.toString(), destinatario.toString(), "  cambio de asesor  ");

        // Act
        EnvioSolicitudCambioAsesorDomain envio =
                EnviarSolicitudCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("cambio de asesor");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
        assertThat(envio.getSolicitud().getRemitente()).isEqualTo(envio.getRemitente().getId());
        assertThat(envio.getSolicitud().getDestinatario()).isEqualTo(envio.getDestinatario().getId());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudCambioAsesorCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), "mensaje");

        // Act
        EnvioSolicitudCambioAsesorDomain envio =
                EnviarSolicitudCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
