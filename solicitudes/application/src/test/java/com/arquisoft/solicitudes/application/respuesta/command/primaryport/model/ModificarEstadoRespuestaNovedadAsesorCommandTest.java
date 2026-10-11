package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificarEstadoRespuestaNovedadAsesorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act
        var command = ModificarEstadoRespuestaNovedadAsesorCommand.crear(
                solicitud.toString(), "APROBADA", asesor);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.asesorUsuario()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularLosErroresDeEntrada_cuandoLosTresDatosSonInvalidos() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadAsesorCommand.crear("  ", "  ", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudNoEsUuid() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaNovedadAsesorCommand.crear(
                        "no-es-uuid", "APROBADA", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }
}
