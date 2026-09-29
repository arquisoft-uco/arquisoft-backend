package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponderSolicitudNovedadCoordinadorCommandTest {

    private static boolean tieneCodigo(ValidationResult resultado, String codigo) {
        return resultado.getErrores().stream().anyMatch(e -> e.codigoError().equals(codigo));
    }

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act
        ResponderSolicitudNovedadCoordinadorCommand command =
                ResponderSolicitudNovedadCoordinadorCommand.crear(
                        solicitud.toString(), "Contenido valido", coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.contenido()).isEqualTo("Contenido valido");
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeRecortarElContenidoAntesDeValidarLaLongitud_cuandoTraeEspaciosAlrededor() {
        // Arrange
        var contenidoDeLongitudMaxima = "x".repeat(100);

        // Act
        var command = ResponderSolicitudNovedadCoordinadorCommand.crear(
                UUID.randomUUID().toString(), "  " + contenidoDeLongitudMaxima + " ", UUID.randomUUID());

        // Assert
        assertThat(command.contenido()).isEqualTo(contenidoDeLongitudMaxima);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElContenidoEstaEnBlanco() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> ResponderSolicitudNovedadCoordinadorCommand.crear(
                        UUID.randomUUID().toString(), "   ", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Respuesta.CONTENIDO)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElContenidoExcede100Caracteres() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> ResponderSolicitudNovedadCoordinadorCommand.crear(
                        UUID.randomUUID().toString(), "x".repeat(101), UUID.randomUUID()));

        // Assert
        assertThat(tieneCodigo(excepcion.getValidationResult(),
                SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO)).isTrue();
    }

    @Test
    void debeAcumularLosErrores_cuandoElIdDeSolicitudNoEsUuidYElCoordinadorEsNulo() {
        // Act
        ApplicationValidationException excepcion = assertThrows(ApplicationValidationException.class,
                () -> ResponderSolicitudNovedadCoordinadorCommand.crear(
                        "no-es-uuid", "contenido", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
