package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadAsesorEliminadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        var solicitudId = UUID.randomUUID();
        var remitenteUsuario = UUID.randomUUID();
        var tipoSolicitud = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId();

        // Act
        var evento = new SolicitudNovedadAsesorEliminadaEvent(solicitudId, remitenteUsuario, tipoSolicitud);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
        assertThat(evento.getTipoSolicitud()).isEqualTo(tipoSolicitud);
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        var evento = new SolicitudNovedadAsesorEliminadaEvent(
                UUID.randomUUID(), UUID.randomUUID(), "NOVEDAD_PARA_EL_ASESOR");

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo("solicitudes.solicitud.novedad_asesor_eliminada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudNovedadAsesorEliminadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudNovedadAsesorEliminadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadAsesorEliminadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
