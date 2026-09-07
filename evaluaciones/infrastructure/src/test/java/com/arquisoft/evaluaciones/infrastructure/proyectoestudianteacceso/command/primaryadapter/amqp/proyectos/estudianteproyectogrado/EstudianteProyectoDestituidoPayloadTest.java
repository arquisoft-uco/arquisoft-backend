package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoDestituidoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class EstudianteProyectoDestituidoEventoDePrueba extends DomainEvent {

        private final UUID proyectoId;
        private final UUID estudianteId;

        private EstudianteProyectoDestituidoEventoDePrueba(UUID proyectoId, UUID estudianteId) {
            super(EventTopics.Proyectos.ESTUDIANTE_PROYECTO_DESTITUIDO, "EstudianteProyectoDestituidoEvent");
            this.proyectoId = proyectoId;
            this.estudianteId = estudianteId;
        }

        public UUID getProyectoId() {
            return proyectoId;
        }

        public UUID getEstudianteId() {
            return estudianteId;
        }
    }

    @Test
    void debeConservarIdEventoOcurridoEnYCamposDeNegocio_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EstudianteProyectoDestituidoEventoDePrueba(UUID.randomUUID(), UUID.randomUUID());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EstudianteProyectoDestituidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoId()).isEqualTo(evento.getProyectoId().toString());
        assertThat(payload.estudianteId()).isEqualTo(evento.getEstudianteId().toString());
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        String json = """
                {"idEvento":"evt-2","proyectoId":"11111111-1111-1111-1111-111111111111",
                 "estudianteId":"22222222-2222-2222-2222-222222222222"}
                """;

        // Act
        var payload = mapper.readValue(json, EstudianteProyectoDestituidoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-2");
    }
}
