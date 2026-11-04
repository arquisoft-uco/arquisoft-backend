package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ArtefactoVersionRepositorioCambiadaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private record ContactoDePrueba(String nombre, String email) {
    }

    private static final class ArtefactoVersionRepositorioCambiadaEventoDePrueba extends DomainEvent {

        private final String documento;
        private final int versionAnterior;
        private final int versionNueva;
        private final List<ContactoDePrueba> estudiantes;
        private final ContactoDePrueba asesor;

        private ArtefactoVersionRepositorioCambiadaEventoDePrueba(
                String documento, int versionAnterior, int versionNueva,
                List<ContactoDePrueba> estudiantes, ContactoDePrueba asesor) {
            super(EventTopics.Artefactos.ARTEFACTO_VERSION_REPOSITORIO_CAMBIADA,
                    "ArtefactoVersionRepositorioCambiadaEvent");
            this.documento = documento;
            this.versionAnterior = versionAnterior;
            this.versionNueva = versionNueva;
            this.estudiantes = estudiantes;
            this.asesor = asesor;
        }

        public String getDocumento() {
            return documento;
        }

        public int getVersionAnterior() {
            return versionAnterior;
        }

        public int getVersionNueva() {
            return versionNueva;
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
        var evento = new ArtefactoVersionRepositorioCambiadaEventoDePrueba(
                "Anteproyecto", 1, 2,
                List.of(new ContactoDePrueba("Laura Gomez", "laura@uco.edu.co"),
                        new ContactoDePrueba("Pedro Ruiz", "pedro@uco.edu.co")),
                new ContactoDePrueba("Ana Perez", "ana.perez@uco.edu.co"));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), ArtefactoVersionRepositorioCambiadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.versionAnterior()).isEqualTo(1);
        assertThat(payload.versionNueva()).isEqualTo(2);
        assertThat(payload.estudiantes()).extracting(
                        ArtefactoVersionRepositorioCambiadaPayload.ContactoPayload::nombre,
                        ArtefactoVersionRepositorioCambiadaPayload.ContactoPayload::email)
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
                {"idEvento":"evt-1","versionAnterior":1,"versionNueva":2}
                """;

        // Act
        var payload = mapper.readValue(json, ArtefactoVersionRepositorioCambiadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.documento()).isNull();
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.asesor()).isNull();
    }
}
