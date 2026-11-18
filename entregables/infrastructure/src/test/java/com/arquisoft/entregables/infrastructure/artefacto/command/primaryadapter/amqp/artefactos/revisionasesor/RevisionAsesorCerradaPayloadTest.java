package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorCerradaPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class CerradaEventoDePrueba extends DomainEvent {

        private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();
        private final UUID artefacto = UtilUUID.generarNuevoUUID();
        private final UUID versionArtefacto = UtilUUID.generarNuevoUUID();
        private final Instant fechaVersion = Instant.parse("2026-11-10T10:15:30.123456Z");
        private final UUID versionRepositorioArtefacto = UtilUUID.generarNuevoUUID();
        private final UUID repositorio = UtilUUID.generarNuevoUUID();

        private CerradaEventoDePrueba() {
            super(EventTopics.Artefactos.REVISION_ASESOR_CERRADA, "RevisionAsesorCerradaEvent");
        }

        public String getDocumento() {
            return "Anteproyecto";
        }

        public int getNumeroVersion() {
            return 3;
        }

        public UUID getProyectoGrado() {
            return proyectoGrado;
        }

        public UUID getArtefacto() {
            return artefacto;
        }

        public UUID getVersionArtefacto() {
            return versionArtefacto;
        }

        public String getUriArchivo() {
            return "pendientes/a.pdf";
        }

        public Instant getFechaVersion() {
            return fechaVersion;
        }

        public UUID getVersionRepositorioArtefacto() {
            return versionRepositorioArtefacto;
        }

        public UUID getRepositorio() {
            return repositorio;
        }

        public String getNombreRepositorio() {
            return "Repositorio";
        }

        public Map<String, String> getAsesor() {
            return Map.of("nombre", "Mario", "email", "mario@uco.edu.co");
        }

        public List<Map<String, String>> getEstudiantes() {
            return List.of(Map.of("nombre", "Ana", "email", "ana@soyuco.edu.co"));
        }
    }

    @Test
    void debeDeserializarEventoReal_cuandoSeSerializa() {
        // Arrange
        var evento = new CerradaEventoDePrueba();

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), RevisionAsesorCerradaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.numeroVersion()).isEqualTo(3);
        assertThat(payload.proyectoGrado()).isEqualTo(evento.proyectoGrado.toString());
        assertThat(payload.artefacto()).isEqualTo(evento.artefacto.toString());
        assertThat(payload.versionArtefacto()).isEqualTo(evento.versionArtefacto.toString());
        assertThat(payload.uriArchivo()).isEqualTo("pendientes/a.pdf");
        assertThat(payload.fechaVersion()).isEqualTo(evento.fechaVersion);
        assertThat(payload.versionRepositorioArtefacto()).isEqualTo(evento.versionRepositorioArtefacto.toString());
        assertThat(payload.repositorio()).isEqualTo(evento.repositorio.toString());
        assertThat(payload.nombreRepositorio()).isEqualTo("Repositorio");
    }

    @Test
    void debeTolerarCamposFaltantes_cuandoElEventoNoLosTrae() {
        // Arrange
        var json = """
                {"idEvento":"evt-1"}
                """;

        // Act
        var payload = mapper.readValue(json, RevisionAsesorCerradaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.artefacto()).isNull();
        assertThat(payload.uriArchivo()).isNull();
        assertThat(payload.fechaVersion()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
