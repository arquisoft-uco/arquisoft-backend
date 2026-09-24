package com.arquisoft.solicitudes.domain.solicitud.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadAsesorEnviadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();

        // Act
        SolicitudNovedadAsesorEnviadaEvent evento = new SolicitudNovedadAsesorEnviadaEvent(
                solicitudId, "Ana Estudiante", "Pedro Asesor", "pedro@uco.edu.co",
                "novedad para el asesor");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Asesor");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("novedad para el asesor");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudNovedadAsesorEnviadaEvent evento = new SolicitudNovedadAsesorEnviadaEvent(
                UUID.randomUUID(), "Ana Estudiante", "Pedro Asesor", "pedro@uco.edu.co", "mensaje");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.solicitud.novedad_asesor_enviada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudNovedadAsesorEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudNovedadAsesorEnviadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadAsesorEnviadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
