package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudCambioAsesorRespondidaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class SolicitudCambioAsesorRespondidaEventoDePrueba extends DomainEvent {

        private final UUID solicitudId;
        private final String contenido;
        private final String remitenteNombre;
        private final String remitenteEmail;
        private final String responsableNombre;

        private SolicitudCambioAsesorRespondidaEventoDePrueba(
                UUID solicitudId, String contenido, String remitenteNombre,
                String remitenteEmail, String responsableNombre) {
            super(EventTopics.Solicitudes.CAMBIO_ASESOR_RESPONDIDA,
                    "SolicitudCambioAsesorRespondidaEvent");
            this.solicitudId = solicitudId;
            this.contenido = contenido;
            this.remitenteNombre = remitenteNombre;
            this.remitenteEmail = remitenteEmail;
            this.responsableNombre = responsableNombre;
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

        public String getResponsableNombre() {
            return responsableNombre;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new SolicitudCambioAsesorRespondidaEventoDePrueba(
                UtilUUID.generarNuevoUUID(), "Se aprueba el cambio de asesor",
                "Ana Estudiante", "ana.est@soyuco.edu.co", "Pedro Coordinador");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), SolicitudCambioAsesorRespondidaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.solicitudId()).isEqualTo(evento.getSolicitudId().toString());
        assertThat(payload.contenido()).isEqualTo(evento.getContenido());
        assertThat(payload.remitenteNombre()).isEqualTo(evento.getRemitenteNombre());
        assertThat(payload.remitenteEmail()).isEqualTo(evento.getRemitenteEmail());
        assertThat(payload.responsableNombre()).isEqualTo(evento.getResponsableNombre());
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","solicitudId":"11111111-1111-1111-1111-111111111111",
                 "contenido":"Se aprueba el cambio de asesor","remitenteNombre":"Ana Estudiante",
                 "remitenteEmail":"ana.est@soyuco.edu.co","responsableNombre":"Pedro Coordinador"}
                """;

        // Act
        var payload = mapper.readValue(json, SolicitudCambioAsesorRespondidaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
