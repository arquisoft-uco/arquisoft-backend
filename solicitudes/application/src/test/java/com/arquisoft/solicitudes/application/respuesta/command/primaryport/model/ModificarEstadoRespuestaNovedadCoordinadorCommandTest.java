package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificarEstadoRespuestaNovedadCoordinadorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();

        // Act
        var command = ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                solicitud.toString(), "APROBADA", coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                        "  ", "APROBADA", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudNoEsUuid() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                        "no-es-uuid", "APROBADA", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElNuevoEstadoEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                        UUID.randomUUID().toString(), "  ", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElCoordinadorUsuarioEsNulo() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                        UUID.randomUUID().toString(), "APROBADA", null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }

    @Test
    void debeAcumularVariosErrores_cuandoLaSolicitudYElNuevoEstadoEstanEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                        "  ", "  ", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
