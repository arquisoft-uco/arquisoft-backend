package com.arquisoft.evaluaciones.infrastructure.entregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoDisponibleEvaluacionPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class DisponibleEventoDePrueba extends DomainEvent {

        private final UUID entregableProyectoGrado;
        private final UUID proyectoGrado;
        private final String nombreProyecto;
        private final int version;

        private DisponibleEventoDePrueba(UUID entregableProyectoGrado, UUID proyectoGrado) {
            super(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION,
                    "EntregableProyectoGradoDisponibleEvaluacionEvent");
            this.entregableProyectoGrado = entregableProyectoGrado;
            this.proyectoGrado = proyectoGrado;
            this.nombreProyecto = "Sistema de riego";
            this.version = 2;
        }

        public UUID getEntregableProyectoGrado() {
            return entregableProyectoGrado;
        }

        public UUID getProyectoGrado() {
            return proyectoGrado;
        }

        public String getNombreProyecto() {
            return nombreProyecto;
        }

        public int getVersion() {
            return version;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new DisponibleEventoDePrueba(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EntregableProyectoGradoDisponibleEvaluacionPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.entregableProyectoGrado()).isEqualTo(evento.getEntregableProyectoGrado().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.getProyectoGrado().toString());
        assertThat(payload.nombreProyecto()).isEqualTo(evento.getNombreProyecto());
        assertThat(payload.version()).isEqualTo(evento.getVersion());
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","entregableProyectoGrado":"11111111-1111-1111-1111-111111111111",
                 "nombreProyecto":"Sistema","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EntregableProyectoGradoDisponibleEvaluacionPayload.class);

        // Assert
        assertThat(payload.version()).isNull();
        assertThat(payload.proyectoGrado()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.nombreProyecto()).isEqualTo("Sistema");
    }
}
