package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadCoordinadorRespondidaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        UUID respuestaId = UUID.randomUUID();

        // Act
        SolicitudNovedadCoordinadorRespondidaEvent evento = new SolicitudNovedadCoordinadorRespondidaEvent(
                solicitudId, respuestaId, "contenido", EstadoRespuesta.EN_REVISION.getId(),
                "Ana Estudiante", "ana@uco.edu.co", "Pedro Coordinador");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRespuestaId()).isEqualTo(respuestaId);
        assertThat(evento.getContenido()).isEqualTo("contenido");
        assertThat(evento.getEstadoRespuesta()).isEqualTo("EN_REVISION");
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getCoordinadorNombre()).isEqualTo("Pedro Coordinador");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudNovedadCoordinadorRespondidaEvent evento = new SolicitudNovedadCoordinadorRespondidaEvent(
                UUID.randomUUID(), UUID.randomUUID(), "c", "EN_REVISION", "Ana", "ana@uco.edu.co", "Pedro");

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo("solicitudes.respuesta.novedad_coordinador_respondida");
        assertThat(evento.getTemaEvento())
                .isEqualTo(SolicitudNovedadCoordinadorRespondidaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento())
                .isEqualTo(SolicitudNovedadCoordinadorRespondidaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorRespondidaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
