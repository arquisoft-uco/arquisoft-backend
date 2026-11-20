package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.primaryadapter.amqp.proyectos.coordinadorproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoRegistradoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class ProyectoGradoRegistradoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final ContactoDePrueba coordinador;

        private ProyectoGradoRegistradoEventoDePrueba(UUID proyectoGradoId, ContactoDePrueba coordinador) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO, "ProyectoGradoRegistradoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.fichaPerfilId = UtilUUID.generarNuevoUUID();
            this.tituloProyecto = "Sistema";
            this.coordinador = coordinador;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public ContactoDePrueba getCoordinador() {
            return coordinador;
        }
    }

    @Test
    void debeConservarIdNombreYCorreoDelCoordinador_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var coordinador = new ContactoDePrueba(UtilUUID.generarNuevoUUID(), "Coord Uno", "coord@uco.edu.co");
        var evento = new ProyectoGradoRegistradoEventoDePrueba(UtilUUID.generarNuevoUUID(), coordinador);

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.coordinador()).isEqualTo(new ProyectoGradoRegistradoPayload.CoordinadorPayload(
                coordinador.id().toString(), "Coord Uno", "coord@uco.edu.co"));
    }

    @Test
    void debeDeserializarConNull_cuandoElEventoNoTraeCoordinadorNiCamposDesconocidos() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444","campoNuevo":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.coordinador()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
