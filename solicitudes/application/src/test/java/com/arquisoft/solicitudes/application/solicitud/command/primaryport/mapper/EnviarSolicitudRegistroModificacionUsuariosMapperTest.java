package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class EnviarSolicitudRegistroModificacionUsuariosMapperTest {

    @Test
    void debeConstruirElBundle_cuandoElComandoEsValido() {
        // Arrange
        var remitente = UtilUUID.generarNuevoUUID();
        var destinatario = UtilUUID.generarNuevoUUID();
        var command = EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                remitente, destinatario.toString(), "  registro y modificacion de usuarios  ");

        // Act
        var envio =
                EnviarSolicitudRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(envio.getSolicitud().getMensajeSolicitud()).isEqualTo("registro y modificacion de usuarios");
        assertThat(envio.getSolicitud().getTipoSolicitud()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
        assertThat(envio.getSolicitud().getRemitenteUsuario()).isEqualTo(envio.getRemitente().getUsuario());
        assertThat(envio.getSolicitud().getDestinatarioUsuario()).isEqualTo(envio.getDestinatario().getUsuario());
    }

    @Test
    void debeGenerarIdsCandidatosDistintos_paraRemitenteYDestinatario() {
        // Arrange
        var command = EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID().toString(), "mensaje");

        // Act
        var envio =
                EnviarSolicitudRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(envio.getRemitente().getId()).isNotNull();
        assertThat(envio.getDestinatario().getId()).isNotNull();
        assertThat(envio.getRemitente().getId()).isNotEqualTo(envio.getDestinatario().getId());
    }
}
