package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRespuestaRegistroModificacionUsuariosMapperTest {

    @Test
    void debeFijarTipoRegistroYModificacionDeUsuarios_cuandoMapeaElCommand() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = EliminarRespuestaRegistroModificacionUsuariosCommand.crear(solicitud.toString(), administrador);

        // Act
        var dominio = EliminarRespuestaRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(administrador);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
