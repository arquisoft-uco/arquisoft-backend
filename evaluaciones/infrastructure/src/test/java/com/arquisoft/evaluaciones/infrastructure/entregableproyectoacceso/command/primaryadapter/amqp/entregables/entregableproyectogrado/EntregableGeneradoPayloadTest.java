package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableGeneradoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class EntregableGeneradoEventoDePrueba extends DomainEvent {

        private final UUID entregableId;
        private final UUID proyectoId;
        private final Integer versionEntregable;

        private EntregableGeneradoEventoDePrueba(UUID entregableId, UUID proyectoId, Integer versionEntregable) {
            super(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO, "EntregableGeneradoEvent");
            this.entregableId = entregableId;
            this.proyectoId = proyectoId;
            this.versionEntregable = versionEntregable;
        }

        public UUID getEntregableId() {
            return entregableId;
        }

        public UUID getProyectoId() {
            return proyectoId;
        }

        public Integer getVersionEntregable() {
            return versionEntregable;
        }
    }

    @Test
    void debeConservarIdEventoOcurridoEnYCamposDeNegocio_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EntregableGeneradoEventoDePrueba(UUID.randomUUID(), UUID.randomUUID(), 3);

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EntregableGeneradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.entregableId()).isEqualTo(evento.getEntregableId().toString());
        assertThat(payload.proyectoId()).isEqualTo(evento.getProyectoId().toString());
        assertThat(payload.versionEntregable()).isEqualTo(3);
    }

    @Test
    void debeDejarVersionEntregableNula_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        String json = """
                {"idEvento":"evt-1","entregableId":"11111111-1111-1111-1111-111111111111",
                 "proyectoId":"22222222-2222-2222-2222-222222222222"}
                """;

        // Act
        var payload = mapper.readValue(json, EntregableGeneradoPayload.class);

        // Assert
        assertThat(payload.versionEntregable()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
