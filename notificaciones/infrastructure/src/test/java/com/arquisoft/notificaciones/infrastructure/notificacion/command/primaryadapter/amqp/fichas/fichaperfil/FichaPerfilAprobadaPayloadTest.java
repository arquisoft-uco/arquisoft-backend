package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilAprobadaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    public record IntegranteDePrueba(UUID estudiante, ContactoDePrueba contacto) {
    }

    private static final class FichaPerfilAprobadaEventoDePrueba extends DomainEvent {

        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final String estadoFicha;
        private final UUID coordinadorId;
        private final ContactoDePrueba asesor;
        private final List<IntegranteDePrueba> estudiantes;

        private FichaPerfilAprobadaEventoDePrueba(UUID fichaPerfilId, ContactoDePrueba asesor,
                                                  List<IntegranteDePrueba> estudiantes) {
            super(EventTopics.Fichas.FICHA_PERFIL_APROBADA, "FichaPerfilAprobadaEvent");
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = "Sistema de gestión";
            this.estadoFicha = "APROBADA_CON_OBSERVACIONES";
            this.coordinadorId = UUID.randomUUID();
            this.asesor = asesor;
            this.estudiantes = estudiantes;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public String getEstadoFicha() {
            return estadoFicha;
        }

        public UUID getCoordinadorId() {
            return coordinadorId;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }

        public List<IntegranteDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarDestinatariosYEstado_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var evento = new FichaPerfilAprobadaEventoDePrueba(UUID.randomUUID(),
                new ContactoDePrueba("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co"),
                List.of(new IntegranteDePrueba(estudiante, new ContactoDePrueba("Ana Gomez", "ana.gomez@soyuco.edu.co"))));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), FichaPerfilAprobadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.estadoFicha()).isEqualTo("APROBADA_CON_OBSERVACIONES");
        assertThat(payload.asesor())
                .isEqualTo(new FichaPerfilAprobadaPayload.ContactoPayload("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co"));
        assertThat(payload.estudiantes()).containsExactly(new FichaPerfilAprobadaPayload.IntegrantePayload(
                estudiante.toString(),
                new FichaPerfilAprobadaPayload.ContactoPayload("Ana Gomez", "ana.gomez@soyuco.edu.co")));
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","fichaPerfilId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","estadoFicha":"APROBADA",
                 "asesor":{"nombre":"Carlos Ruiz","email":"carlos.ruiz@soyuco.edu.co"},"estudiantes":[]}
                """;

        // Act
        var payload = mapper.readValue(json, FichaPerfilAprobadaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
