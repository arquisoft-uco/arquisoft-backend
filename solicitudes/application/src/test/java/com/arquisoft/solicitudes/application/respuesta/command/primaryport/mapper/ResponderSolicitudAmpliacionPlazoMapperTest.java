package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudAmpliacionPlazoMapperTest {

    @Test
    void debeConstruirElObjetoDeAccionConTipoAmpliacionDePlazo_cuandoElComandoEsValido() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = new ResponderSolicitudAmpliacionPlazoCommand(solicitud, "contenido", coordinador);

        // Act
        var accion = ResponderSolicitudAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getRespuesta().getContenido()).isEqualTo("contenido");
        assertThat(accion.getRespuesta().getEstadoRespuesta()).isEqualTo(EstadoRespuesta.EN_REVISION);
        assertThat(accion.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(accion.getTipoEsperado()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
