package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarRespuestaNovedadCoordinadorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act
        EliminarRespuestaNovedadCoordinadorCommand command =
                EliminarRespuestaNovedadCoordinadorCommand.crear(
                        solicitud.toString(), coordinador.toString());

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudEstaEnBlanco() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarRespuestaNovedadCoordinadorCommand.crear("  ", UUID.randomUUID().toString()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudNoEsUuid() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarRespuestaNovedadCoordinadorCommand.crear(
                        "no-es-uuid", UUID.randomUUID().toString()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElCoordinadorUsuarioNoEsUuid() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarRespuestaNovedadCoordinadorCommand.crear(
                        UUID.randomUUID().toString(), "tampoco-es-uuid"));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
