package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudRegistroModificacionUsuariosMapperTest {

    @Test
    void debeConstruirElObjetoDeAccionConTipoRegistroYModificacionDeUsuarios_cuandoElComandoEsValido() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = new ResponderSolicitudRegistroModificacionUsuariosCommand(solicitud, "contenido", administrador);

        // Act
        var accion = ResponderSolicitudRegistroModificacionUsuariosMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getRespuesta().getContenido()).isEqualTo("contenido");
        assertThat(accion.getRespuesta().getEstadoRespuesta()).isEqualTo(EstadoRespuesta.EN_REVISION);
        assertThat(accion.getResponsableUsuario()).isEqualTo(administrador);
        assertThat(accion.getTipoEsperado()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
