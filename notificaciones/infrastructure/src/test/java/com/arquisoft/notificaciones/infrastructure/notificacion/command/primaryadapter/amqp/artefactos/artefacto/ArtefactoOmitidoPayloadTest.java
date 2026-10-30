package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ArtefactoOmitidoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private record ContactoDePrueba(String nombre, String email) {
    }

    private static final class ArtefactoOmitidoEventoDePrueba extends DomainEvent {

        private final String documento;
        private final List<ContactoDePrueba> estudiantes;
        private final ContactoDePrueba asesor;

        private ArtefactoOmitidoEventoDePrueba(
                String documento, List<ContactoDePrueba> estudiantes, ContactoDePrueba asesor) {
            super(EventTopics.Artefactos.ARTEFACTO_OMITIDO, "ArtefactoOmitidoEvent");
            this.documento = documento;
            this.estudiantes = estudiantes;
            this.asesor = asesor;
        }

        public String getDocumento() {
            return documento;
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }
    }

    @Test
    void debeDeserializarEventoReal() {
        // Arrange
        var evento = new ArtefactoOmitidoEventoDePrueba(
                "Anteproyecto",
                List.of(new ContactoDePrueba("Laura Gomez", "laura@uco.edu.co"),
                        new ContactoDePrueba("Pedro Ruiz", "pedro@uco.edu.co")),
                new ContactoDePrueba("Ana Perez", "ana.perez@uco.edu.co"));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ArtefactoOmitidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.estudiantes()).extracting(
                        ArtefactoOmitidoPayload.ContactoPayload::nombre,
                        ArtefactoOmitidoPayload.ContactoPayload::email)
                .containsExactly(
                        tuple("Laura Gomez", "laura@uco.edu.co"),
                        tuple("Pedro Ruiz", "pedro@uco.edu.co"));
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
        var payload = mapper.readValue(json, ArtefactoOmitidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.documento()).isNull();
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.asesor()).isNull();
    }
}
