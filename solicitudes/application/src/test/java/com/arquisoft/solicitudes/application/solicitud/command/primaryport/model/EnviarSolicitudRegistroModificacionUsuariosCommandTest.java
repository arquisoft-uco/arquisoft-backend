package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnviarSolicitudRegistroModificacionUsuariosCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var remitente = UtilUUID.generarNuevoUUID();
        var destinatario = UtilUUID.generarNuevoUUID();

        // Act
        var command =
                EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                        remitente, destinatario.toString(), "  Solicito registro y modificacion de usuarios  ");

        // Assert
        assertThat(command.remitenteUsuario()).isEqualTo(remitente);
        assertThat(command.destinatarioUsuario()).isEqualTo(destinatario);
        assertThat(command.mensajeSolicitud()).isEqualTo("Solicito registro y modificacion de usuarios");
    }

    @Test
    void debeAcumularLosErroresDeFormato_cuandoLosDatosSonInvalidos() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                        null, "tampoco-es-uuid", "a".repeat(101)));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }

    @Test
    void debeReportarElMensaje_cuandoEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID().toString(), "   "));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }
}
