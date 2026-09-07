package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadCoordinadorEliminadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        UUID remitenteUsuario = UUID.randomUUID();
        String tipoSolicitud = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

        // Act
        SolicitudNovedadCoordinadorEliminadaEvent evento =
                new SolicitudNovedadCoordinadorEliminadaEvent(solicitudId, remitenteUsuario, tipoSolicitud);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
        assertThat(evento.getTipoSolicitud()).isEqualTo(tipoSolicitud);
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudNovedadCoordinadorEliminadaEvent evento = new SolicitudNovedadCoordinadorEliminadaEvent(
                UUID.randomUUID(), UUID.randomUUID(), "NOVEDAD_PARA_EL_COORDINADOR");

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo("solicitudes.solicitud.novedad_coordinador_eliminada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudNovedadCoordinadorEliminadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudNovedadCoordinadorEliminadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorEliminadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
