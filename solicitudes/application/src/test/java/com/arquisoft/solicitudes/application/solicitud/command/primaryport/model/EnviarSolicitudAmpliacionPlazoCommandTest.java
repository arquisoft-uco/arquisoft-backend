package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnviarSolicitudAmpliacionPlazoCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();

        // Act
        EnviarSolicitudAmpliacionPlazoCommand command =
                EnviarSolicitudAmpliacionPlazoCommand.crear(
                        remitente.toString(), destinatario.toString(), "  Solicito ampliacion de plazo  ");

        // Assert
        assertThat(command.remitenteUsuario()).isEqualTo(remitente);
        assertThat(command.destinatarioUsuario()).isEqualTo(destinatario);
        assertThat(command.mensajeSolicitud()).isEqualTo("Solicito ampliacion de plazo");
    }

    @Test
    void debeAcumularLosErroresDeFormato_cuandoLosDatosSonInvalidos() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EnviarSolicitudAmpliacionPlazoCommand.crear(
                        "no-es-uuid", "tampoco-es-uuid", "a".repeat(101)));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }

    @Test
    void debeReportarElMensaje_cuandoEstaEnBlanco() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EnviarSolicitudAmpliacionPlazoCommand.crear(
                        UUID.randomUUID().toString(), UUID.randomUUID().toString(), "   "));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }
}
