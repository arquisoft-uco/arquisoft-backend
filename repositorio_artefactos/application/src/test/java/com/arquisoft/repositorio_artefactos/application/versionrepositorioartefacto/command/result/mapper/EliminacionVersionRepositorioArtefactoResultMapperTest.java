package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminacionVersionRepositorioArtefactoResultMapperTest {

    @Test
    void debeConstruirUnaFabricaPorVariante() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(version);

        // Act
        var eliminada = EliminacionVersionRepositorioArtefactoResultMapper.toResultEliminada(eliminacion);
        var inexistente = EliminacionVersionRepositorioArtefactoResultMapper.toResultInexistente(eliminacion);

        // Assert
        assertThat(eliminada.version()).isEqualTo(version);
        assertThat(inexistente.version()).isEqualTo(version);
    }
}
