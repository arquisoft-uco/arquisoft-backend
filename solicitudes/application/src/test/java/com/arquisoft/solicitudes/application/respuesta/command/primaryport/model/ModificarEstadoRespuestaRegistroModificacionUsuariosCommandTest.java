package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificarEstadoRespuestaRegistroModificacionUsuariosCommandTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarEstadoRespuestaRegistroModificacionUsuariosCommand.crear(
                solicitud, "APROBADA", administrador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.administradorUsuario()).isEqualTo(administrador);
    }

    @Test
    void debeAcumularErrores_cuandoVariosDatosSonInvalidos() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaRegistroModificacionUsuariosCommand.crear(null, "  ", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
