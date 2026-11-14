package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoEstadoCambiadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class EstadoCambiadoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final List<ContactoDePrueba> estudiantes;
        private final List<ContactoDePrueba> asesores;

        private EstadoCambiadoEventoDePrueba(UUID proyectoGradoId) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO, "ProyectoGradoEstadoCambiadoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.estudiantes = List.of(new ContactoDePrueba(UUID.randomUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co"));
            this.asesores = List.of(new ContactoDePrueba(null, "Mario Peña", "mario.pena@uco.edu.co"));
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return "Sistema de gestión";
        }

        public String getEstadoAnterior() {
            return "ATRASADO";
        }

        public String getEstadoNuevo() {
            return "CANCELADO";
        }

        public String getMotivo() {
            return "Falta de avance";
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }

        public List<ContactoDePrueba> getAsesores() {
            return asesores;
        }
    }

    @Test
    void debeDeserializar_cuandoLlegaElContratoCompleto() {
        // Arrange
        var evento = new EstadoCambiadoEventoDePrueba(UUID.randomUUID());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.estadoAnterior()).isEqualTo("ATRASADO");
        assertThat(payload.estadoNuevo()).isEqualTo("CANCELADO");
        assertThat(payload.motivo()).isEqualTo("Falta de avance");
        assertThat(payload.estudiantes()).containsExactly(
                new ProyectoGradoEstadoCambiadoPayload.ContactoPayload("Ana Ruiz", "ana.ruiz@soyuco.edu.co"));
        assertThat(payload.asesores()).containsExactly(
                new ProyectoGradoEstadoCambiadoPayload.ContactoPayload("Mario Peña", "mario.pena@uco.edu.co"));
    }

    @Test
    void debeDeserializarConNulo_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.estadoNuevo()).isNull();
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.asesores()).isNull();
    }
}
