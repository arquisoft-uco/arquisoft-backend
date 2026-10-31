package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.estudianteartefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteArtefactoRemovidoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private record ContactoDePrueba(String nombre, String email) {
    }

    private static final class EstudianteArtefactoRemovidoEventoDePrueba extends DomainEvent {

        private final String documento;
        private final ContactoDePrueba estudiante;
        private final ContactoDePrueba asesor;

        private EstudianteArtefactoRemovidoEventoDePrueba(
                String documento, ContactoDePrueba estudiante, ContactoDePrueba asesor) {
            super(EventTopics.Artefactos.ESTUDIANTE_ARTEFACTO_REMOVIDO, "EstudianteArtefactoRemovidoEvent");
            this.documento = documento;
            this.estudiante = estudiante;
            this.asesor = asesor;
        }

        public String getDocumento() {
            return documento;
        }

        public ContactoDePrueba getEstudiante() {
            return estudiante;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }
    }

    @Test
    void debeDeserializarEventoReal() {
        // Arrange
        var evento = new EstudianteArtefactoRemovidoEventoDePrueba(
                "Anteproyecto",
                new ContactoDePrueba("Laura Gomez", "laura@uco.edu.co"),
                new ContactoDePrueba("Ana Perez", "ana.perez@uco.edu.co"));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EstudianteArtefactoRemovidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.estudiante().nombre()).isEqualTo("Laura Gomez");
        assertThat(payload.estudiante().email()).isEqualTo("laura@uco.edu.co");
        assertThat(payload.asesor().nombre()).isEqualTo("Ana Perez");
        assertThat(payload.asesor().email()).isEqualTo("ana.perez@uco.edu.co");
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1"}
                """;

        // Act
        var payload = mapper.readValue(json, EstudianteArtefactoRemovidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.documento()).isNull();
        assertThat(payload.estudiante()).isNull();
        assertThat(payload.asesor()).isNull();
    }
}
