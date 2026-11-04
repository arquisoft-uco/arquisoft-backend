package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarSolicitudCambioAsesorMapperTest {

    @Test
    void debeCopiarSolicitudYRemitente_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var remitente = UtilUUID.generarNuevoUUID();
        var command = EliminarSolicitudCambioAsesorCommand.crear(solicitud.toString(), remitente);

        // Act
        var dominio = EliminarSolicitudCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getRemitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeFijarElTipoEsperadoDelFlujo_cuandoMapeaElComando() {
        // Arrange
        var command = EliminarSolicitudCambioAsesorCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID());

        // Act
        var dominio = EliminarSolicitudCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
