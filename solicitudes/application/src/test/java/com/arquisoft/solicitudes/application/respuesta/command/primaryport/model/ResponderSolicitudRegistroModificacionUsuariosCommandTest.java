package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponderSolicitudRegistroModificacionUsuariosCommandTest {

    private static boolean tieneCodigo(ValidationResult resultado, String codigo) {
        return resultado.getErrores().stream().anyMatch(e -> e.codigoError().equals(codigo));
    }

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var command =
                ResponderSolicitudRegistroModificacionUsuariosCommand.crear(
                        solicitud, "Contenido valido", administrador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.contenido()).isEqualTo("Contenido valido");
        assertThat(command.administradorUsuario()).isEqualTo(administrador);
    }

    @Test
    void debeRecortarElContenidoAntesDeValidarLaLongitud_cuandoTraeEspaciosAlrededor() {
        // Arrange
        var contenidoDeLongitudMaxima = "x".repeat(100);

        // Act
        var command = ResponderSolicitudRegistroModificacionUsuariosCommand.crear(
                UtilUUID.generarNuevoUUID(), "  " + contenidoDeLongitudMaxima + " ", UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(command.contenido()).isEqualTo(contenidoDeLongitudMaxima);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElContenidoEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ResponderSolicitudRegistroModificacionUsuariosCommand.crear(
                        UtilUUID.generarNuevoUUID(), "   ",UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Respuesta.CONTENIDO)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElContenidoExcede100Caracteres() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ResponderSolicitudRegistroModificacionUsuariosCommand.crear(
                        UtilUUID.generarNuevoUUID(), "x".repeat(101), UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(tieneCodigo(excepcion.getValidationResult(),
                SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO)).isTrue();
    }

    @Test
    void debeAcumularLosErrores_cuandoLaSolicitudYElAdministradorSonNulos() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ResponderSolicitudRegistroModificacionUsuariosCommand.crear(
                        null, "contenido", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
