package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarSolicitudRegistroModificacionUsuariosMapperTest {

    @Test
    void debeCopiarSolicitudYRemitente_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var remitente = UtilUUID.generarNuevoUUID();
        var command = EliminarSolicitudRegistroModificacionUsuariosCommand.crear(solicitud.toString(), remitente);

        // Act
        var dominio = EliminarSolicitudRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getRemitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debeFijarElTipoEsperadoDeRegistroModificacionUsuarios_cuandoMapeaElComando() {
        // Arrange
        var command = EliminarSolicitudRegistroModificacionUsuariosCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID());

        // Act
        var dominio = EliminarSolicitudRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
