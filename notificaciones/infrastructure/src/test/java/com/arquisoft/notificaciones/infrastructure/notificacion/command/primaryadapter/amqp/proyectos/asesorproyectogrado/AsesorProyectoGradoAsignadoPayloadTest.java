package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoAsignadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class AsesorAsignadoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final ContactoDePrueba asesor;

        private AsesorAsignadoEventoDePrueba(UUID proyectoGradoId, ContactoDePrueba asesor) {
            super(EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO, "AsesorProyectoGradoAsignadoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = "Sistema de gestión";
            this.asesor = asesor;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }
    }

    @Test
    void debeConservarElAsesor_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new AsesorAsignadoEventoDePrueba(UtilUUID.generarNuevoUUID(),
                new ContactoDePrueba("Luis Diaz", "luis.diaz@soyuco.edu.co"));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), AsesorProyectoGradoAsignadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.asesor()).isEqualTo(
                new AsesorProyectoGradoAsignadoPayload.ContactoPayload("Luis Diaz", "luis.diaz@soyuco.edu.co"));
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444",
                 "tituloProyecto":"Sistema","asesor":{"nombre":"Luis Diaz","email":"luis.diaz@soyuco.edu.co"}}
                """;

        // Act
        var payload = mapper.readValue(json, AsesorProyectoGradoAsignadoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
