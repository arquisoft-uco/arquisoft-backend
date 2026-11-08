package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class VersionArtefactoMapperTest {

    @Test
    void debeMapearTamanoEnAmbosSentidos() {
        // Arrange
        var entity = new VersionArtefactoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 3,
                Instant.parse("2026-11-07T10:00:00Z"), "versiones/uri", 2048L);

        // Act
        var domain = VersionArtefactoMapper.toDomain(entity);
        var devuelta = VersionArtefactoMapper.toEntity(domain);

        // Assert
        assertThat(domain.getTamanoBytes()).isEqualTo(2048L);
        assertThat(devuelta).isEqualTo(entity);
    }
}
