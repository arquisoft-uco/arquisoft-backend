package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.versionartefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VersionArtefactoGeneradaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private record ContactoDePrueba(String nombre, String email) {
    }

    private static final class VersionArtefactoGeneradaEventoDePrueba extends DomainEvent {

        private final UUID versionArtefacto;
        private final UUID artefacto;
        private final int numeroVersion;
        private final String nombreDocumento;
        private final ContactoDePrueba asesor;

        private VersionArtefactoGeneradaEventoDePrueba(
                UUID versionArtefacto, UUID artefacto, int numeroVersion,
                String nombreDocumento, ContactoDePrueba asesor) {
            super(EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA, "VersionArtefactoGeneradaEvent");
            this.versionArtefacto = versionArtefacto;
            this.artefacto = artefacto;
            this.numeroVersion = numeroVersion;
            this.nombreDocumento = nombreDocumento;
            this.asesor = asesor;
        }

        public UUID getVersionArtefacto() {
            return versionArtefacto;
        }

        public UUID getArtefacto() {
            return artefacto;
        }

        public int getNumeroVersion() {
            return numeroVersion;
        }

        public String getNombreDocumento() {
            return nombreDocumento;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }
    }

    @Test
    void debeDeserializarEventoReal() {
        // Arrange
        var evento = new VersionArtefactoGeneradaEventoDePrueba(UUID.randomUUID(), UUID.randomUUID(), 3,
                "Formato A", new ContactoDePrueba("Ana Perez", "ana.perez@uco.edu.co"));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), VersionArtefactoGeneradaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.versionArtefacto()).isEqualTo(evento.getVersionArtefacto().toString());
        assertThat(payload.artefacto()).isEqualTo(evento.getArtefacto().toString());
        assertThat(payload.numeroVersion()).isEqualTo(3);
        assertThat(payload.nombreDocumento()).isEqualTo("Formato A");
        assertThat(payload.asesor().nombre()).isEqualTo("Ana Perez");
        assertThat(payload.asesor().email()).isEqualTo("ana.perez@uco.edu.co");
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","numeroVersion":2}
                """;

        // Act
        var payload = mapper.readValue(json, VersionArtefactoGeneradaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.asesor()).isNull();
        assertThat(payload.nombreDocumento()).isNull();
    }
}
