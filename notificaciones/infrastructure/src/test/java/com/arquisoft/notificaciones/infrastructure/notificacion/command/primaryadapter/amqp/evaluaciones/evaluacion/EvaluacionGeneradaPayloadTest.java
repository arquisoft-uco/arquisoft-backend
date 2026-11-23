package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionGeneradaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class EvaluacionGeneradaEventoDePrueba extends DomainEvent {

        private final UUID evaluacionId = UtilUUID.generarNuevoUUID();
        private final UUID entregableId = UtilUUID.generarNuevoUUID();
        private final UUID proyectoGradoId = UtilUUID.generarNuevoUUID();
        private final String nombreProyecto = "Sistema de gestión";
        private final int versionEntregable = 3;
        private final ContactoDePrueba coordinador =
                new ContactoDePrueba(UtilUUID.generarNuevoUUID(), "Carlos Coord", "carlos@uco.edu.co");

        private EvaluacionGeneradaEventoDePrueba() {
            super(EventTopics.Evaluaciones.EVALUACION_GENERADA, "EvaluacionGeneradaEvent");
        }

        public UUID getEvaluacionId() {
            return evaluacionId;
        }

        public UUID getEntregableId() {
            return entregableId;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getNombreProyecto() {
            return nombreProyecto;
        }

        public int getVersionEntregable() {
            return versionEntregable;
        }

        public ContactoDePrueba getCoordinador() {
            return coordinador;
        }
    }

    @Test
    void debeConservarLosCamposQueNecesitaElCorreo_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EvaluacionGeneradaEventoDePrueba();

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EvaluacionGeneradaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.evaluacionId()).isEqualTo(evento.getEvaluacionId().toString());
        assertThat(payload.entregableId()).isEqualTo(evento.getEntregableId().toString());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.nombreProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.versionEntregable()).isEqualTo(3);
        assertThat(payload.coordinador())
                .isEqualTo(new EvaluacionGeneradaPayload.ContactoPayload("Carlos Coord", "carlos@uco.edu.co"));
    }

    @Test
    void debeDeserializarConNulls_cuandoElEventoLlegaSinCamposOConCamposDesconocidos() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","nombreProyecto":"P","campoNuevo":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EvaluacionGeneradaPayload.class);

        // Assert
        assertThat(payload.nombreProyecto()).isEqualTo("P");
        assertThat(payload.coordinador()).isNull();
        assertThat(payload.versionEntregable()).isNull();
    }
}
