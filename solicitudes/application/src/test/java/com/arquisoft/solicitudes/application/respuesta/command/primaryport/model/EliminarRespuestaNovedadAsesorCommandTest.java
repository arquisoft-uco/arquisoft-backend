package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarRespuestaNovedadAsesorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act
        var command = EliminarRespuestaNovedadAsesorCommand.crear(solicitud.toString(), asesor);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.asesorUsuario()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarRespuestaNovedadAsesorCommand.crear("  ", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudNoEsUuid() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarRespuestaNovedadAsesorCommand.crear("no-es-uuid", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElAsesorUsuarioEsNulo() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarRespuestaNovedadAsesorCommand.crear(UUID.randomUUID().toString(), null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
