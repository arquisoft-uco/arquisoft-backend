package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudCambioAsesorMapperTest {

    @Test
    void debeConstruirElObjetoDeAccionConTipoCambioDeAsesor_cuandoElComandoEsValido() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = new ResponderSolicitudCambioAsesorCommand(solicitud, "contenido", coordinador);

        // Act
        var accion = ResponderSolicitudCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getRespuesta().getContenido()).isEqualTo("contenido");
        assertThat(accion.getRespuesta().getEstadoRespuesta()).isEqualTo(EstadoRespuesta.EN_REVISION);
        assertThat(accion.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(accion.getTipoEsperado()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
