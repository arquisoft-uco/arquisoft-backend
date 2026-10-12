package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudNovedadCoordinadorMapperTest {

    @Test
    void debeMapearLosTresCampos_cuandoConvierteElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var command = new ResponderSolicitudNovedadCoordinadorCommand(solicitud, "contenido", coordinador);

        // Act
        var accion =
                ResponderSolicitudNovedadCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getRespuesta().getContenido()).isEqualTo("contenido");
        assertThat(accion.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(accion.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }
}
