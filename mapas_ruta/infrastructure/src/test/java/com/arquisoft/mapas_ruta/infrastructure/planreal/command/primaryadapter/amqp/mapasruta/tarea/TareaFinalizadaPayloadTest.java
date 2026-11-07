package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.amqp.mapasruta.tarea;

import com.arquisoft.mapas_ruta.domain.tarea.event.TareaFinalizadaEvent;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class TareaFinalizadaPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeDeserializarEventoReal_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new TareaFinalizadaEvent(UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), TareaFinalizadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.tarea()).isEqualTo(evento.getTarea().toString());
    }

    @Test
    void debeTolerarCampoAusente_cuandoFaltaTareaYSobraUnDesconocido() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, TareaFinalizadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.tarea()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
