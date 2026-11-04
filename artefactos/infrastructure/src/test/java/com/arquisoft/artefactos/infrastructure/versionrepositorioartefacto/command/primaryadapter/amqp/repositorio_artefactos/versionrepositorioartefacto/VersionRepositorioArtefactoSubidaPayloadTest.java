package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.versionrepositorioartefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoSubidaPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class VersionSubidaEventoDePrueba extends DomainEvent {

        private final UUID versionRepositorioArtefacto;
        private final String nombreDocumento;
        private final int versionDocumento;
        private final UUID repositorioArtefacto;
        private final String nombreRepositorio;
        private final String nombreArchivo;
        private final String tipoContenido;
        private final long tamanoBytes;

        private VersionSubidaEventoDePrueba(
                UUID versionRepositorioArtefacto, UUID repositorioArtefacto) {
            super(EventTopics.RepositorioArtefactos.VERSION_REPOSITORIO_ARTEFACTO_SUBIDA,
                    "VersionRepositorioArtefactoSubidaEvent");
            this.versionRepositorioArtefacto = versionRepositorioArtefacto;
            this.nombreDocumento = "Plantilla de tesis";
            this.versionDocumento = 3;
            this.repositorioArtefacto = repositorioArtefacto;
            this.nombreRepositorio = "Repositorio base";
            this.nombreArchivo = "plantilla.pdf";
            this.tipoContenido = "application/pdf";
            this.tamanoBytes = 2048L;
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

        public String getNombreRepositorio() {
            return nombreRepositorio;
        }

        public String getNombreArchivo() {
            return nombreArchivo;
        }

        public String getTipoContenido() {
            return tipoContenido;
        }

        public long getTamanoBytes() {
            return tamanoBytes;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new VersionSubidaEventoDePrueba(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), VersionRepositorioArtefactoSubidaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.versionRepositorioArtefacto())
                .isEqualTo(evento.getVersionRepositorioArtefacto().toString());
        assertThat(payload.nombreDocumento()).isEqualTo(evento.getNombreDocumento());
        assertThat(payload.versionDocumento()).isEqualTo(evento.getVersionDocumento());
        assertThat(payload.repositorioArtefacto()).isEqualTo(evento.getRepositorioArtefacto().toString());
        assertThat(payload.nombreRepositorio()).isEqualTo(evento.getNombreRepositorio());
        assertThat(payload.nombreArchivo()).isEqualTo(evento.getNombreArchivo());
        assertThat(payload.tipoContenido()).isEqualTo(evento.getTipoContenido());
        assertThat(payload.tamanoBytes()).isEqualTo(evento.getTamanoBytes());
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","versionRepositorioArtefacto":"11111111-1111-1111-1111-111111111111",
                 "nombreDocumento":"Plantilla","versionDocumento":1,"campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, VersionRepositorioArtefactoSubidaPayload.class);

        // Assert
        assertThat(payload.tamanoBytes()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.nombreDocumento()).isEqualTo("Plantilla");
        assertThat(payload.versionDocumento()).isEqualTo(1);
    }
}
