package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarSolicitudAmpliacionPlazoMapperTest {

    @Test
    void debeCopiarSolicitudYRemitente_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var remitente = UtilUUID.generarNuevoUUID();
        var command = EliminarSolicitudAmpliacionPlazoCommand.crear(solicitud.toString(), remitente);

        // Act
        var dominio = EliminarSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getRemitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeFijarElTipoEsperadoDeAmpliacionPlazo_cuandoMapeaElComando() {
        // Arrange
        var command = EliminarSolicitudAmpliacionPlazoCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID());

        // Act
        var dominio = EliminarSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
