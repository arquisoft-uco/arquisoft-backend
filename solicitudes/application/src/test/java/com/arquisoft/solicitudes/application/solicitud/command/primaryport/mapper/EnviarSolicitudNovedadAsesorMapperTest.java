package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudNovedadAsesorMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                remitente.toString(), destinatario.toString(), "  novedad  ");

        // Act
        EnvioSolicitudNovedadAsesorDomain envio =
                EnviarSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("novedad");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
        assertThat(envio.getSolicitud().getRemitente()).isEqualTo(envio.getRemitente().getId());
        assertThat(envio.getSolicitud().getDestinatario()).isEqualTo(envio.getDestinatario().getId());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), "mensaje");

        // Act
        EnvioSolicitudNovedadAsesorDomain envio =
                EnviarSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
