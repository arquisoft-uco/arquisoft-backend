package com.arquisoft.solicitudes.domain.solicitud.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudCambioAsesorEnviadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        String remitente = UUID.randomUUID().toString();
        String destinatario = UUID.randomUUID().toString();
        LocalDateTime fechaCreacion = LocalDateTime.now();

        // Act
        SolicitudCambioAsesorEnviadaEvent evento = new SolicitudCambioAsesorEnviadaEvent(
                solicitudId, remitente, destinatario, "cambio de asesor",
                fechaCreacion, "CAMBIO_DE_ASESOR");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteUsuarioId()).isEqualTo(remitente);
        assertThat(evento.getDestinatarioUsuarioId()).isEqualTo(destinatario);
        assertThat(evento.getMensajeSolicitud()).isEqualTo("cambio de asesor");
        assertThat(evento.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(evento.getTipoSolicitud()).isEqualTo("CAMBIO_DE_ASESOR");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudCambioAsesorEnviadaEvent evento = new SolicitudCambioAsesorEnviadaEvent(
                UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                "mensaje", LocalDateTime.now(), "CAMBIO_DE_ASESOR");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.solicitud.cambio_asesor_enviada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudCambioAsesorEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudCambioAsesorEnviadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudCambioAsesorEnviadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
