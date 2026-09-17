package com.arquisoft.solicitudes.domain.solicitud.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudCambioAsesorEnviadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();

        // Act
        SolicitudCambioAsesorEnviadaEvent evento = new SolicitudCambioAsesorEnviadaEvent(
                solicitudId, "Ana Estudiante", "Pedro Coordinador", "pedro@uco.edu.co",
                "cambio de asesor");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Coordinador");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("cambio de asesor");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudCambioAsesorEnviadaEvent evento = new SolicitudCambioAsesorEnviadaEvent(
                UUID.randomUUID(), "Ana Estudiante", "Pedro Coordinador", "pedro@uco.edu.co", "mensaje");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.solicitud.cambio_asesor_enviada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudCambioAsesorEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudCambioAsesorEnviadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudCambioAsesorEnviadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
