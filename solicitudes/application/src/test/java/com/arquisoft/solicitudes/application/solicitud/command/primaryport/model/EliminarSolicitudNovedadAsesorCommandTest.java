package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarSolicitudNovedadAsesorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();

        // Act
        var command = EliminarSolicitudNovedadAsesorCommand.crear(solicitud.toString(), remitente);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.remitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeAcumularLosErroresDeEntrada_cuandoElIdNoEsUuidYElRemitenteEsNulo() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarSolicitudNovedadAsesorCommand.crear("no-es-uuid", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarSolicitudNovedadAsesorCommand.crear("  ", UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }
}
