package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoCanceladoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    public record ContactoEstudianteDePrueba(UUID id, String nombre, String email) {
    }

    private static final class ProyectoGradoCanceladoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final String motivo;
        private final List<ContactoEstudianteDePrueba> estudiantes;
        private final List<ContactoDePrueba> asesores;

        private ProyectoGradoCanceladoEventoDePrueba(UUID proyectoGradoId,
                                                     List<ContactoEstudianteDePrueba> estudiantes,
                                                     List<ContactoDePrueba> asesores) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_CANCELADO, "ProyectoGradoCanceladoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = "Sistema de gestión";
            this.motivo = "Falta de avance";
            this.estudiantes = estudiantes;
            this.asesores = asesores;
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

        public List<ContactoEstudianteDePrueba> getEstudiantes() {
            return estudiantes;
        }

        public List<ContactoDePrueba> getAsesores() {
            return asesores;
        }
    }

    @Test
    void debeDeserializarEventoReal_cuandoSeSerializa() {
        // Arrange
        var evento = new ProyectoGradoCanceladoEventoDePrueba(UUID.randomUUID(),
                List.of(new ContactoEstudianteDePrueba(UUID.randomUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co")),
                List.of(new ContactoDePrueba("Mario Peña", "mario.pena@uco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoCanceladoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.motivo()).isEqualTo("Falta de avance");
        assertThat(payload.estudiantes()).containsExactly(
                new ProyectoGradoCanceladoPayload.ContactoPayload("Ana Ruiz", "ana.ruiz@soyuco.edu.co"));
        assertThat(payload.asesores()).containsExactly(
                new ProyectoGradoCanceladoPayload.ContactoPayload("Mario Peña", "mario.pena@uco.edu.co"));
    }

    @Test
    void debeTolerarCampoFaltante_cuandoJsonIncompleto() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoCanceladoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.motivo()).isNull();
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.asesores()).isNull();
    }
}
