package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaFechaFinCambiadaPayloadTest {

    private record EventoFechaFinCambiada(
            String idEvento, Instant ocurridoEn, UUID mapaRuta, UUID proyectoGrado,
            LocalDate fechaInicio, LocalDate fechaFinAnterior, LocalDate fechaFin) {
    }

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeDeserializarEventoReal_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EventoFechaFinCambiada("evt-2", Instant.parse("2026-11-19T10:00:00Z"),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 18), LocalDate.of(2026, 12, 20));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), MapaRutaFechaFinCambiadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-2");
        assertThat(payload.ocurridoEn()).isEqualTo(evento.ocurridoEn());
        assertThat(payload.mapaRuta()).isEqualTo(evento.mapaRuta().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.proyectoGrado().toString());
        assertThat(payload.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 20));
    }

    @Test
    void debeTolerarCampoAusente_cuandoFaltaFechaFin() {
        // Act
        var payload = mapper.readValue("{\"idEvento\":\"evt-2\"}", MapaRutaFechaFinCambiadaPayload.class);

        // Assert
        assertThat(payload.fechaFin()).isNull();
    }
}
