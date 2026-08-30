package com.arquisoft.solicitudes.domain.solicitud.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadAsesorEnviadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        String remitente = UUID.randomUUID().toString();
        String destinatario = UUID.randomUUID().toString();
        LocalDateTime fechaCreacion = LocalDateTime.now();

        // Act
        SolicitudNovedadAsesorEnviadaEvent evento = new SolicitudNovedadAsesorEnviadaEvent(
                solicitudId, remitente, destinatario, "novedad para el asesor",
                fechaCreacion, "NOVEDAD_PARA_EL_ASESOR");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteUsuarioId()).isEqualTo(remitente);
        assertThat(evento.getDestinatarioUsuarioId()).isEqualTo(destinatario);
        assertThat(evento.getMensajeSolicitud()).isEqualTo("novedad para el asesor");
        assertThat(evento.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(evento.getTipoSolicitud()).isEqualTo("NOVEDAD_PARA_EL_ASESOR");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudNovedadAsesorEnviadaEvent evento = new SolicitudNovedadAsesorEnviadaEvent(
                UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                "mensaje", LocalDateTime.now(), "NOVEDAD_PARA_EL_ASESOR");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.solicitud.novedad_asesor_enviada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudNovedadAsesorEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudNovedadAsesorEnviadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadAsesorEnviadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
