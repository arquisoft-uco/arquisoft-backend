package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.artefactos.versionrepositorioartefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoEliminacionAutorizadaPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class EliminacionAutorizadaEventoDePrueba extends DomainEvent {

        private final UUID versionRepositorioArtefacto;
        private final String nombreDocumento;
        private final int versionDocumento;
        private final UUID repositorioArtefacto;

        private EliminacionAutorizadaEventoDePrueba(UUID versionRepositorioArtefacto, UUID repositorioArtefacto) {
            super(EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA,
                    "VersionRepositorioArtefactoEliminacionAutorizadaEvent");
            this.versionRepositorioArtefacto = versionRepositorioArtefacto;
            this.nombreDocumento = "Formato A";
            this.versionDocumento = 2;
            this.repositorioArtefacto = repositorioArtefacto;
        }

        public UUID getVersionRepositorioArtefacto() {
            return versionRepositorioArtefacto;
        }

        public String getNombreDocumento() {
            return nombreDocumento;
        }

        public int getVersionDocumento() {
            return versionDocumento;
        }

        public UUID getRepositorioArtefacto() {
            return repositorioArtefacto;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EliminacionAutorizadaEventoDePrueba(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), VersionRepositorioArtefactoEliminacionAutorizadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.versionRepositorioArtefacto())
                .isEqualTo(evento.getVersionRepositorioArtefacto().toString());
        assertThat(payload.nombreDocumento()).isEqualTo(evento.getNombreDocumento());
        assertThat(payload.versionDocumento()).isEqualTo(evento.getVersionDocumento());
        assertThat(payload.repositorioArtefacto()).isEqualTo(evento.getRepositorioArtefacto().toString());
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","versionRepositorioArtefacto":"11111111-1111-1111-1111-111111111111",
                 "campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, VersionRepositorioArtefactoEliminacionAutorizadaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.repositorioArtefacto()).isNull();
        assertThat(payload.versionDocumento()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
