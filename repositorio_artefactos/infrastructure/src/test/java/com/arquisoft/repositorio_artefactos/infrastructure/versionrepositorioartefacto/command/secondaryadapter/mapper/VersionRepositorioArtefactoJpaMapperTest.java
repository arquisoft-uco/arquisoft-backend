package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoJpaMapperTest {

    @Test
    void debeMapearTodosLosCampos() {
        // Arrange
        var entity = new VersionRepositorioArtefactoEntity(UtilUUID.generarNuevoUUID(), "Formato A", 3,
                Instant.now(), UtilUUID.generarNuevoUUID(), "versiones/x", "application/pdf", 2048L,
                "formato.pdf", "a".repeat(64));

        // Act
        var jpa = VersionRepositorioArtefactoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getNombreDocumento()).isEqualTo("Formato A");
        assertThat(jpa.getNumeroVersion()).isEqualTo(3);
        assertThat(jpa.getFechaCreacion()).isEqualTo(entity.fechaCreacion());
        assertThat(jpa.getRepositorioArtefacto()).isEqualTo(entity.repositorioArtefacto());
        assertThat(jpa.getUriArchivo()).isEqualTo("versiones/x");
        assertThat(jpa.getTipoContenido()).isEqualTo("application/pdf");
        assertThat(jpa.getTamanoBytes()).isEqualTo(2048L);
        assertThat(jpa.getNombreArchivo()).isEqualTo("formato.pdf");
        assertThat(jpa.getHashSha256()).isEqualTo("a".repeat(64));
    }
}
