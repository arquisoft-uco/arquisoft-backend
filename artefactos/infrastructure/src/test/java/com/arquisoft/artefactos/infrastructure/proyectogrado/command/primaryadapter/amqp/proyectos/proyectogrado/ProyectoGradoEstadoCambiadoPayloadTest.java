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

class ProyectoGradoEstadoCambiadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class EstadoCambiadoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final String estadoAnterior;
        private final String estadoNuevo;
        private final String motivo;
        private final List<Map<String, String>> estudiantes;
        private final List<Map<String, String>> asesores;

        private EstadoCambiadoEventoDePrueba(UUID proyectoGradoId, String estadoNuevo) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO, "ProyectoGradoEstadoCambiadoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = "Sistema de gestion";
            this.estadoAnterior = "EN_PROCESO";
            this.estadoNuevo = estadoNuevo;
            this.motivo = "";
            this.estudiantes = List.of(Map.of("nombre", "Luis", "email", "luis@uco.edu.co"));
            this.asesores = List.of(Map.of("nombre", "Mario", "email", "mario@uco.edu.co"));
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public String getEstadoAnterior() {
            return estadoAnterior;
        }

        public String getEstadoNuevo() {
            return estadoNuevo;
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
    void debeDeserializarEventoDelProductor_ignorandoCamposExtra() {
        // Arrange
        var evento = new EstadoCambiadoEventoDePrueba(UtilUUID.generarNuevoUUID(), "FINALIZADO");

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.estadoNuevo()).isEqualTo("FINALIZADO");
    }

    @Test
    void debeDeserializarConNulo_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.proyectoGradoId()).isNull();
        assertThat(payload.estadoNuevo()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
