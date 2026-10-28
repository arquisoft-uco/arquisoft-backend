package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorEstadoCambiadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class EventoDePrueba extends DomainEvent {

        private final UUID revisionAsesorId;
        private final String estado;
        private final String asesorNombre;
        private final String asesorEmail;
        private final String estudianteNombre;

        private EventoDePrueba(UUID revisionAsesorId, String estado, String asesorNombre, String asesorEmail,
                               String estudianteNombre) {
            super(EventTopics.Artefactos.REVISION_ASESOR_ESTADO_CAMBIADO, "RevisionAsesorEstadoCambiadoEvent");
            this.revisionAsesorId = revisionAsesorId;
            this.estado = estado;
            this.asesorNombre = asesorNombre;
            this.asesorEmail = asesorEmail;
            this.estudianteNombre = estudianteNombre;
        }

        public UUID getRevisionAsesorId() {
            return revisionAsesorId;
        }

        public String getEstado() {
            return estado;
        }

        public String getAsesorNombre() {
            return asesorNombre;
        }

        public String getAsesorEmail() {
            return asesorEmail;
        }

        public String getEstudianteNombre() {
            return estudianteNombre;
        }
    }

    @Test
    void debeConservarIdEventoYCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EventoDePrueba(UtilUUID.generarNuevoUUID(), "RESUELTA", "Ana Gomez", "ana@uco.edu.co", "Luis Perez");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), RevisionAsesorEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.revisionAsesorId()).isEqualTo(evento.getRevisionAsesorId().toString());
        assertThat(payload.estado()).isEqualTo("RESUELTA");
        assertThat(payload.asesorNombre()).isEqualTo("Ana Gomez");
        assertThat(payload.asesorEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(payload.estudianteNombre()).isEqualTo("Luis Perez");
    }

    @Test
    void debeIgnorarCamposDesconocidosYDejarOcurridoEnNulo_cuandoElProductorEvoluciona() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","revisionAsesorId":"11111111-1111-1111-1111-111111111111",
                 "estado":"RESUELTA","asesorNombre":"Ana","asesorEmail":"ana@uco.edu.co",
                 "estudianteNombre":"Luis","campoNuevo":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, RevisionAsesorEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
