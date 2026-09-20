package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadAsesorRespuestaEliminadaEventTest {

    @Test
    void debeConstruirElEventoValido_conElTopicYLosCamposEsperados() {
        // Arrange
        var solicitudId = UUID.randomUUID();
        var asesorUsuario = UUID.randomUUID();
        var tipoSolicitud = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId();

        // Act
        var evento = new SolicitudNovedadAsesorRespuestaEliminadaEvent(
                solicitudId, asesorUsuario, tipoSolicitud);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getAsesorUsuario()).isEqualTo(asesorUsuario);
        assertThat(evento.getTipoSolicitud()).isEqualTo(tipoSolicitud);
        assertThat(evento.getTemaEvento()).matches("^[a-z]+\\.[a-z_]+\\.[a-z_]+$");
        assertThat(evento.getTemaEvento())
                .isEqualTo("solicitudes.respuesta.novedad_asesor_respuesta_eliminada");
        assertThat(evento.getTemaEvento())
                .isEqualTo(SolicitudNovedadAsesorRespuestaEliminadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento())
                .isEqualTo(SolicitudNovedadAsesorRespuestaEliminadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadAsesorRespuestaEliminadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
