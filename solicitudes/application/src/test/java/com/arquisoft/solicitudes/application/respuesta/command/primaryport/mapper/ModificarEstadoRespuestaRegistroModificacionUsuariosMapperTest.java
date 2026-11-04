package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarEstadoRespuestaRegistroModificacionUsuariosMapperTest {

    @Test
    void debeFijarTipoRegistroModificacionUsuarios_cuandoMapeaElCommand() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = ModificarEstadoRespuestaRegistroModificacionUsuariosCommand.crear(
                solicitud, "APROBADA", administrador);

        // Act
        var dominio = ModificarEstadoRespuestaRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(administrador);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
        assertThat(dominio.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
