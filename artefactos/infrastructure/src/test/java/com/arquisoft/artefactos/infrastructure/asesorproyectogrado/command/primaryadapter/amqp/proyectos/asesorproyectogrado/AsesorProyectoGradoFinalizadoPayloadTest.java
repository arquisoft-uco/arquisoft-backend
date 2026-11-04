package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoFinalizadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class FinalizadoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final UUID asesorId;

        private FinalizadoEventoDePrueba(UUID proyectoGradoId, String tituloProyecto, UUID asesorId) {
            super(EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_FINALIZADO, "AsesorProyectoGradoFinalizadoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = tituloProyecto;
            this.asesorId = asesorId;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public UUID getAsesorId() {
            return asesorId;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new FinalizadoEventoDePrueba(UtilUUID.generarNuevoUUID(), "Sistema", UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), AsesorProyectoGradoFinalizadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.asesorId()).isEqualTo(evento.getAsesorId().toString());
    }

    @Test
    void debeDeserializarConNull_cuandoElEventoAnteriorNoTraeAsesorId() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, AsesorProyectoGradoFinalizadoPayload.class);

        // Assert
        assertThat(payload.asesorId()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
