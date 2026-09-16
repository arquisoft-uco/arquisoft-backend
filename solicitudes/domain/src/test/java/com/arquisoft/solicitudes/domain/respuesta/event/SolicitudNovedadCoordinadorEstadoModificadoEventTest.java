package com.arquisoft.solicitudes.domain.respuesta.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadCoordinadorEstadoModificadoEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        var solicitudId = UUID.randomUUID();

        // Act
        var evento = new SolicitudNovedadCoordinadorEstadoModificadoEvent(
                solicitudId, "APROBADA", "Aprobada",
                "Ana Estudiante", "ana@uco.edu.co", "Pedro Coordinador");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getNuevoEstado()).isEqualTo("APROBADA");
        assertThat(evento.getNuevoEstadoNombre()).isEqualTo("Aprobada");
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getCoordinadorNombre()).isEqualTo("Pedro Coordinador");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        var evento = new SolicitudNovedadCoordinadorEstadoModificadoEvent(
                UUID.randomUUID(), "NO_APROBADA", "No aprobada", "Ana", "ana@uco.edu.co", "Pedro");

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo("solicitudes.respuesta.novedad_coordinador_estado_modificado");
        assertThat(evento.getTemaEvento())
                .isEqualTo(SolicitudNovedadCoordinadorEstadoModificadoEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento())
                .isEqualTo(SolicitudNovedadCoordinadorEstadoModificadoEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorEstadoModificadoEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
