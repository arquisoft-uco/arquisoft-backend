package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudNovedadAsesorMapperTest {

    @Test
    void debeMapearLosTresCampos_cuandoConvierteElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var command = new ResponderSolicitudNovedadAsesorCommand(solicitud, "contenido", asesor);

        // Act
        RespuestaSolicitudDomain accion =
                ResponderSolicitudNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getRespuesta().getContenido()).isEqualTo("contenido");
        assertThat(accion.getResponsableUsuario()).isEqualTo(asesor);
        assertThat(accion.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
    }
}
