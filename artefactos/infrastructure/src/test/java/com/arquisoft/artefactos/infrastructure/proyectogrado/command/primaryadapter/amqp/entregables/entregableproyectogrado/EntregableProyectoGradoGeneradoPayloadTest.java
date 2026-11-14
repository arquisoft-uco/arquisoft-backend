package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoGeneradoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class EntregableGeneradoEventoDePrueba extends DomainEvent {

        private final UUID entregableProyectoGrado;
        private final UUID proyectoGrado;
        private final int version;

        private EntregableGeneradoEventoDePrueba(UUID entregableProyectoGrado, UUID proyectoGrado, int version) {
            super(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO, "EntregableProyectoGradoGeneradoEvent");
            this.entregableProyectoGrado = entregableProyectoGrado;
            this.proyectoGrado = proyectoGrado;
            this.version = version;
        }

        public UUID getEntregableProyectoGrado() {
            return entregableProyectoGrado;
        }

        public UUID getProyectoGrado() {
            return proyectoGrado;
        }

        public int getVersion() {
            return version;
        }
    }

    @Test
    void debeDeserializarEventoReal_conTodosLosCampos() {
        // Arrange
        var evento = new EntregableGeneradoEventoDePrueba(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 4);

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EntregableProyectoGradoGeneradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.entregableProyectoGrado()).isEqualTo(evento.getEntregableProyectoGrado().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.getProyectoGrado().toString());
        assertThat(payload.version()).isEqualTo(4);
    }

    @Test
    void debeDeserializarConNulo_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","entregableProyectoGrado":"11111111-1111-1111-1111-111111111111"}
                """;

        // Act
        var payload = mapper.readValue(json, EntregableProyectoGradoGeneradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.version()).isNull();
        assertThat(payload.proyectoGrado()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
