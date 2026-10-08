package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.estadofichaperfil;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilAgregadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class EstadoFichaPerfilAgregadoEventoDePrueba extends DomainEvent {

        private final UUID estadoFichaPerfilId;
        private final UUID fichaPerfilId;
        private final List<ContactoDePrueba> estudiantes;

        private EstadoFichaPerfilAgregadoEventoDePrueba(UUID estadoFichaPerfilId, UUID fichaPerfilId,
                                                        List<ContactoDePrueba> estudiantes) {
            super(EventTopics.Fichas.ESTADO_FICHA_PERFIL_AGREGADO, "EstadoFichaPerfilAgregadoEvent");
            this.estadoFichaPerfilId = estadoFichaPerfilId;
            this.fichaPerfilId = fichaPerfilId;
            this.estudiantes = estudiantes;
        }

        public UUID getEstadoFichaPerfilId() {
            return estadoFichaPerfilId;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return "Sistema de gestión";
        }

        public String getEstadoFicha() {
            return "DESCARTADA";
        }

        public String getEstadoFichaNombre() {
            return "Descartada";
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarLosDatosYLosEstudiantes_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EstadoFichaPerfilAgregadoEventoDePrueba(UUID.randomUUID(), UUID.randomUUID(),
                List.of(new ContactoDePrueba("Ana Gomez", "ana.gomez@soyuco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EstadoFichaPerfilAgregadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.estadoFichaPerfilId()).isEqualTo(evento.getEstadoFichaPerfilId().toString());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.estadoFicha()).isEqualTo("DESCARTADA");
        assertThat(payload.estadoFichaNombre()).isEqualTo("Descartada");
        assertThat(payload.estudiantes()).containsExactly(
                new EstadoFichaPerfilAgregadoPayload.ContactoPayload("Ana Gomez", "ana.gomez@soyuco.edu.co"));
    }

    @Test
    void debeDejarLosCamposAusentesEnNulo_cuandoElProductorNoLosEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","fichaPerfilId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","estadoFicha":"DESCARTADA"}
                """;

        // Act
        var payload = mapper.readValue(json, EstadoFichaPerfilAgregadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.estadoFichaNombre()).isNull();
        assertThat(payload.estudiantes()).isNull();
    }
}
