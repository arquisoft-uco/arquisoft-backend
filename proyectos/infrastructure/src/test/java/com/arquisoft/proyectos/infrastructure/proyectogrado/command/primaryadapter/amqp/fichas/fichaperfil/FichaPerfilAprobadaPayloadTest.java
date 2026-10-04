package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.fichas.fichaperfil;

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

        private FichaPerfilAprobadaEventoDePrueba(UUID fichaPerfilId, UUID coordinadorId,
                                                  List<IntegranteDePrueba> estudiantes) {
            super(EventTopics.Fichas.FICHA_PERFIL_APROBADA, "FichaPerfilAprobadaEvent");
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = "Sistema de gestión";
            this.estadoFicha = "APROBADA";
            this.coordinadorId = coordinadorId;
            this.asesor = new ContactoDePrueba("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co");
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
    void debeLeerLosCamposQueUsaProyectosEIgnorarElResto_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var evento = new FichaPerfilAprobadaEventoDePrueba(UUID.randomUUID(), UUID.randomUUID(),
                List.of(new IntegranteDePrueba(estudiante, new ContactoDePrueba("Ana", "ana@soyuco.edu.co"))));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), FichaPerfilAprobadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.coordinadorId()).isEqualTo(evento.getCoordinadorId().toString());
        assertThat(payload.estudiantes())
                .extracting(FichaPerfilAprobadaPayload.IntegrantePayload::estudiante)
                .containsExactly(estudiante.toString());
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","fichaPerfilId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","coordinadorId":"22222222-2222-2222-2222-222222222222",
                 "estudiantes":[]}
                """;

        // Act
        var payload = mapper.readValue(json, FichaPerfilAprobadaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.estudiantes()).isEmpty();
    }
}
