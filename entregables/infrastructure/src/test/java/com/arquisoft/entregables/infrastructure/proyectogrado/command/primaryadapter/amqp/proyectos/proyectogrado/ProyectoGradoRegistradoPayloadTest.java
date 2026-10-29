package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoRegistradoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class RegistradoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;

        private RegistradoEventoDePrueba(UUID proyectoGradoId, String tituloProyecto) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO, "ProyectoGradoRegistradoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = tituloProyecto;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new RegistradoEventoDePrueba(UtilUUID.generarNuevoUUID(), "Sistema de gestion");

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestion");
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.tituloProyecto()).isNull();
        assertThat(payload.proyectoGradoId()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
