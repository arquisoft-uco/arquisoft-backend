package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.repositorioartefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorioArtefactoModificadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class RepositorioModificadoEventoDePrueba extends DomainEvent {

        private final UUID repositorioArtefacto;
        private final String nombreRepositorio;

        private RepositorioModificadoEventoDePrueba(UUID repositorioArtefacto) {
            super(EventTopics.RepositorioArtefactos.REPOSITORIO_ARTEFACTO_MODIFICADO,
                    "RepositorioArtefactoModificadoEvent");
            this.repositorioArtefacto = repositorioArtefacto;
            this.nombreRepositorio = "Repositorio nuevo";
        }

        public UUID getRepositorioArtefacto() {
            return repositorioArtefacto;
        }

        public String getNombreRepositorio() {
            return nombreRepositorio;
        }
    }

    @Test
    void debeDeserializarEventoReal_cuandoSeSerializa() {
        // Arrange
        var evento = new RepositorioModificadoEventoDePrueba(UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), RepositorioArtefactoModificadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.repositorioArtefacto()).isEqualTo(evento.getRepositorioArtefacto().toString());
        assertThat(payload.nombreRepositorio()).isEqualTo(evento.getNombreRepositorio());
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","repositorioArtefacto":"11111111-1111-1111-1111-111111111111",
                 "campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, RepositorioArtefactoModificadoPayload.class);

        // Assert
        assertThat(payload.nombreRepositorio()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
