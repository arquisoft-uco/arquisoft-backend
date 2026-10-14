package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadAsesorEstadoModificadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class SolicitudNovedadAsesorEstadoModificadoEventoDePrueba extends DomainEvent {

        private final UUID solicitudId;
        private final String nuevoEstado;
        private final String nuevoEstadoNombre;
        private final String remitenteNombre;
        private final String remitenteEmail;
        private final String responsableNombre;

        private SolicitudNovedadAsesorEstadoModificadoEventoDePrueba(
                UUID solicitudId, String nuevoEstado, String nuevoEstadoNombre,
                String remitenteNombre, String remitenteEmail, String responsableNombre) {
            super(EventTopics.Solicitudes.NOVEDAD_ASESOR_ESTADO_MODIFICADO,
                    "SolicitudNovedadAsesorEstadoModificadoEvent");
            this.solicitudId = solicitudId;
            this.nuevoEstado = nuevoEstado;
            this.nuevoEstadoNombre = nuevoEstadoNombre;
            this.remitenteNombre = remitenteNombre;
            this.remitenteEmail = remitenteEmail;
            this.responsableNombre = responsableNombre;
        }

        public UUID getSolicitudId() {
            return solicitudId;
        }

        public String getNuevoEstado() {
            return nuevoEstado;
        }

        public String getNuevoEstadoNombre() {
            return nuevoEstadoNombre;
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
        var evento = new SolicitudNovedadAsesorEstadoModificadoEventoDePrueba(
                UUID.randomUUID(), "APROBADA", "Aprobada",
                "Ana Estudiante", "ana.est@soyuco.edu.co", "Pedro Asesor");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), SolicitudNovedadAsesorEstadoModificadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.solicitudId()).isEqualTo(evento.getSolicitudId().toString());
        assertThat(payload.nuevoEstado()).isEqualTo(evento.getNuevoEstado());
        assertThat(payload.nuevoEstadoNombre()).isEqualTo(evento.getNuevoEstadoNombre());
        assertThat(payload.remitenteNombre()).isEqualTo(evento.getRemitenteNombre());
        assertThat(payload.remitenteEmail()).isEqualTo(evento.getRemitenteEmail());
        assertThat(payload.responsableNombre()).isEqualTo(evento.getResponsableNombre());
    }

    @Test
    void debeDejarNuevoEstadoNombreNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","solicitudId":"11111111-1111-1111-1111-111111111111",
                 "nuevoEstado":"APROBADA","remitenteNombre":"Ana Estudiante",
                 "remitenteEmail":"ana.est@soyuco.edu.co","responsableNombre":"Pedro Asesor"}
                """;

        // Act
        var payload = mapper.readValue(json, SolicitudNovedadAsesorEstadoModificadoPayload.class);

        // Assert
        assertThat(payload.nuevoEstadoNombre()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
