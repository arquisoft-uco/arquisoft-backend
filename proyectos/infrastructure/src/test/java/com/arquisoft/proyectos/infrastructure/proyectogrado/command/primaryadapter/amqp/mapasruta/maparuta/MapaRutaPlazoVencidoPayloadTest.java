package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaPlazoVencidoPayloadTest {

    private record EventoPlazoVencido(
            String idEvento, Instant ocurridoEn, UUID mapaRuta, UUID proyectoGrado, LocalDate fechaFin) {
    }

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeDeserializarEventoReal_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EventoPlazoVencido("evt-1", Instant.parse("2026-11-19T05:05:00Z"),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), LocalDate.of(2026, 11, 18));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), MapaRutaPlazoVencidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isEqualTo(evento.ocurridoEn());
        assertThat(payload.mapaRuta()).isEqualTo(evento.mapaRuta().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.proyectoGrado().toString());
        assertThat(payload.fechaFin()).isEqualTo(LocalDate.of(2026, 11, 18));
    }

    @Test
    void debeTolerarCampoAusente_cuandoFaltaFechaFinYSobraUnDesconocido() {
        // Act
        var payload = mapper.readValue(
                "{\"idEvento\":\"evt-1\",\"campoDesconocido\":\"x\"}", MapaRutaPlazoVencidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.fechaFin()).isNull();
        assertThat(payload.proyectoGrado()).isNull();
    }
}
