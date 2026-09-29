package com.arquisoft.solicitudes.domain.solicitud.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudAmpliacionPlazoEnviadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();

        // Act
        SolicitudAmpliacionPlazoEnviadaEvent evento = new SolicitudAmpliacionPlazoEnviadaEvent(
                solicitudId, "Ana Estudiante", "Pedro Coordinador", "pedro@uco.edu.co",
                "ampliacion de plazo");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Coordinador");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("ampliacion de plazo");
    }

    @Test
    void debeExponerElTemaYElTipoDeEvento() {
        // Act
        SolicitudAmpliacionPlazoEnviadaEvent evento = new SolicitudAmpliacionPlazoEnviadaEvent(
                UUID.randomUUID(), "Ana Estudiante", "Pedro Coordinador", "pedro@uco.edu.co", "mensaje");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.solicitud.ampliacion_plazo_enviada");
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudAmpliacionPlazoEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getTipoEvento()).isEqualTo(SolicitudAmpliacionPlazoEnviadaEvent.EVENT_TYPE);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudAmpliacionPlazoEnviadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
