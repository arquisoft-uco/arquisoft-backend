package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoGeneradoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class GeneradoEventoDePrueba extends DomainEvent {

        private final UUID entregableProyectoGrado = UUID.randomUUID();
        private final UUID proyectoGrado = UUID.randomUUID();
        private final String nombreProyecto = "Sistema de riego";
        private final int version = 3;
        private final String estadoEntregable = "NUEVO";
        private final String estadoEntregableNombre = "Nuevo";
        private final List<ContactoDePrueba> estudiantes =
                List.of(new ContactoDePrueba(UUID.randomUUID(), "Ana Gomez", "ana@uco.edu.co"));

        private GeneradoEventoDePrueba() {
            super(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO, "EntregableProyectoGradoGeneradoEvent");
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

        public String getEstadoEntregable() {
            return estadoEntregable;
        }

        public String getEstadoEntregableNombre() {
            return estadoEntregableNombre;
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarCamposYContactosIgnorandoElIdDelEstudiante_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new GeneradoEventoDePrueba();

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EntregableProyectoGradoGeneradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.entregableProyectoGrado()).isEqualTo(evento.getEntregableProyectoGrado().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.getProyectoGrado().toString());
        assertThat(payload.nombreProyecto()).isEqualTo("Sistema de riego");
        assertThat(payload.version()).isEqualTo(3);
        assertThat(payload.estadoEntregable()).isEqualTo("NUEVO");
        assertThat(payload.estadoEntregableNombre()).isEqualTo("Nuevo");
        assertThat(payload.estudiantes()).containsExactly(new ContactoEstudiantePayload("Ana Gomez", "ana@uco.edu.co"));
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaLaListaDeEstudiantes() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","entregableProyectoGrado":"44444444-4444-4444-4444-444444444444",
                 "nombreProyecto":"Sistema","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EntregableProyectoGradoGeneradoPayload.class);

        // Assert
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
