package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoFinalizadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class FinalizadoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final List<Map<String, String>> estudiantes;

        private FinalizadoEventoDePrueba(UUID proyectoGradoId, String tituloProyecto) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_FINALIZADO, "ProyectoGradoFinalizadoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = tituloProyecto;
            this.estudiantes = List.of(Map.of("nombre", "Luis", "email", "luis@uco.edu.co"));
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public List<Map<String, String>> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeDeserializarEventoDelProductor_ignorandoCamposExtra() {
        // Arrange
        var evento = new FinalizadoEventoDePrueba(UtilUUID.generarNuevoUUID(), "Sistema de gestion");

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoFinalizadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
    }

    @Test
    void debeDeserializarConNulo_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoFinalizadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.proyectoGradoId()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
