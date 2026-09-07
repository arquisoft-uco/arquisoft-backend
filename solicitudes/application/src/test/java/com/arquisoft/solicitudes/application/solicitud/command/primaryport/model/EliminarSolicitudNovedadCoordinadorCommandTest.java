package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarSolicitudNovedadCoordinadorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();

        // Act
        EliminarSolicitudNovedadCoordinadorCommand command =
                EliminarSolicitudNovedadCoordinadorCommand.crear(
                        solicitud.toString(), remitente.toString());

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.remitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeAcumularLosErroresDeEntrada_cuandoAmbosIdentificadoresSonInvalidos() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarSolicitudNovedadCoordinadorCommand.crear("no-es-uuid", "tampoco"));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudEstaEnBlanco() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarSolicitudNovedadCoordinadorCommand.crear("  ", UUID.randomUUID().toString()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }
}
