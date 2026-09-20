package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadAsesorRespondidaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class SolicitudNovedadAsesorRespondidaEventoDePrueba extends DomainEvent {

        private final UUID solicitudId;
        private final String contenido;
        private final String remitenteNombre;
        private final String remitenteEmail;
        private final String asesorNombre;

        private SolicitudNovedadAsesorRespondidaEventoDePrueba(
                UUID solicitudId, String contenido, String remitenteNombre,
                String remitenteEmail, String asesorNombre) {
            super(EventTopics.Solicitudes.NOVEDAD_ASESOR_RESPONDIDA,
                    "SolicitudNovedadAsesorRespondidaEvent");
            this.solicitudId = solicitudId;
            this.contenido = contenido;
            this.remitenteNombre = remitenteNombre;
            this.remitenteEmail = remitenteEmail;
            this.asesorNombre = asesorNombre;
        }

        public UUID getSolicitudId() {
            return solicitudId;
        }

        public String getContenido() {
            return contenido;
        }

        public String getRemitenteNombre() {
            return remitenteNombre;
        }

        public String getRemitenteEmail() {
            return remitenteEmail;
        }

        public String getAsesorNombre() {
            return asesorNombre;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new SolicitudNovedadAsesorRespondidaEventoDePrueba(
                UUID.randomUUID(), "Puedes presentar la novedad el lunes",
                "Ana Estudiante", "ana.est@soyuco.edu.co", "Pedro Asesor");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), SolicitudNovedadAsesorRespondidaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.solicitudId()).isEqualTo(evento.getSolicitudId().toString());
        assertThat(payload.contenido()).isEqualTo(evento.getContenido());
        assertThat(payload.remitenteNombre()).isEqualTo(evento.getRemitenteNombre());
        assertThat(payload.remitenteEmail()).isEqualTo(evento.getRemitenteEmail());
        assertThat(payload.asesorNombre()).isEqualTo(evento.getAsesorNombre());
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        String json = """
                {"idEvento":"evt-1","solicitudId":"11111111-1111-1111-1111-111111111111",
                 "contenido":"Puedes presentar la novedad el lunes","remitenteNombre":"Ana Estudiante",
                 "remitenteEmail":"ana.est@soyuco.edu.co","asesorNombre":"Pedro Asesor"}
                """;

        // Act
        var payload = mapper.readValue(json, SolicitudNovedadAsesorRespondidaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
