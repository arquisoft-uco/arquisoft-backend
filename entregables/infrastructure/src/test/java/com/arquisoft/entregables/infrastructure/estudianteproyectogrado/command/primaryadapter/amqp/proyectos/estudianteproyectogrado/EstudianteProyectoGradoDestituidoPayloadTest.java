package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoDestituidoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class DestituidoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final UUID estudianteId;

        private DestituidoEventoDePrueba(UUID proyectoGradoId, UUID estudianteId) {
            super(EventTopics.Proyectos.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO, "EstudianteProyectoGradoDestituidoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.estudianteId = estudianteId;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public UUID getEstudianteId() {
            return estudianteId;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new DestituidoEventoDePrueba(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EstudianteProyectoGradoDestituidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.estudianteId()).isEqualTo(evento.getEstudianteId().toString());
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EstudianteProyectoGradoDestituidoPayload.class);

        // Assert
        assertThat(payload.estudianteId()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
