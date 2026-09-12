package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadCoordinadorRespuestaEliminadaEventTest {

    @Test
    void debeConstruirElEventoValido_conElTopicYLosCamposEsperados() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        UUID coordinadorUsuario = UUID.randomUUID();
        String tipoSolicitud = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

        // Act
        SolicitudNovedadCoordinadorRespuestaEliminadaEvent evento =
                new SolicitudNovedadCoordinadorRespuestaEliminadaEvent(
                        solicitudId, coordinadorUsuario, tipoSolicitud);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getCoordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(evento.getTipoSolicitud()).isEqualTo(tipoSolicitud);
        assertThat(evento.getTemaEvento()).matches("^[a-z]+\\.[a-z_]+\\.[a-z_]+$");
        assertThat(evento.getTemaEvento())
                .isEqualTo("solicitudes.respuesta.novedad_coordinador_respuesta_eliminada");
        assertThat(evento.getTemaEvento())
                .isEqualTo(SolicitudNovedadCoordinadorRespuestaEliminadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento())
                .isEqualTo(SolicitudNovedadCoordinadorRespuestaEliminadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorRespuestaEliminadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
