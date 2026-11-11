package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

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

class ProyectoGradoCanceladoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class CanceladoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final String motivo;
        private final List<Map<String, String>> estudiantes;
        private final List<Map<String, String>> asesores;

        private CanceladoEventoDePrueba(UUID proyectoGradoId) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_CANCELADO, "ProyectoGradoCanceladoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = "Sistema de gestion";
            this.motivo = "Falta de avance";
            this.estudiantes = List.of(Map.of("nombre", "Ana", "email", "ana@soyuco.edu.co"));
            this.asesores = List.of(Map.of("nombre", "Mario", "email", "mario@uco.edu.co"));
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public String getMotivo() {
            return motivo;
        }

        public List<Map<String, String>> getEstudiantes() {
            return estudiantes;
        }

        public List<Map<String, String>> getAsesores() {
            return asesores;
        }
    }

    @Test
    void debeDeserializarEventoReal_cuandoSeSerializa() {
        // Arrange
        var evento = new CanceladoEventoDePrueba(UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoCanceladoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
    }

    @Test
    void debeTolerarCampoFaltante_cuandoJsonIncompleto() {
        // Arrange
        var json = """
                {"idEvento":"evt-1"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoCanceladoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.proyectoGradoId()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
