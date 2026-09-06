package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnviarSolicitudNovedadCoordinadorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();

        // Act
        EnviarSolicitudNovedadCoordinadorCommand command =
                EnviarSolicitudNovedadCoordinadorCommand.crear(
                        remitente.toString(), destinatario.toString(), "  Hola coordinador  ");

        // Assert
        assertThat(command.remitenteUsuario()).isEqualTo(remitente);
        assertThat(command.destinatarioUsuario()).isEqualTo(destinatario);
        assertThat(command.mensajeSolicitud()).isEqualTo("Hola coordinador");
    }

    @Test
    void debeAcumularLosErroresDeFormato_cuandoLosDatosSonInvalidos() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EnviarSolicitudNovedadCoordinadorCommand.crear(
                        "no-es-uuid", "tampoco-es-uuid", "a".repeat(101)));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }
}
