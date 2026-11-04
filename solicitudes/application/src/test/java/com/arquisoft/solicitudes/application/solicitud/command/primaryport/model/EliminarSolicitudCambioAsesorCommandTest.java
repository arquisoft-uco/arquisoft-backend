package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarSolicitudCambioAsesorCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var remitente = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarSolicitudCambioAsesorCommand.crear(solicitud.toString(), remitente);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.remitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeAcumularLosErroresDeEntrada_cuandoElIdNoEsUuidYElRemitenteEsNulo() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarSolicitudCambioAsesorCommand.crear("no-es-uuid", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(ApplicationValidationException.class,
                () -> EliminarSolicitudCambioAsesorCommand.crear("  ", UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }
}
