package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoJpaMapperTest {

    @Test
    void debeConservarLosOchoCampos_cuandoMapeaAJpaEntity() {
        // Arrange
        var entity = new VersionRepositorioArtefactoEntity(
                UtilUUID.generarNuevoUUID(), "Plantilla", 2, UtilUUID.generarNuevoUUID(), "Repositorio",
                "plantilla.pdf", "application/pdf", 2048L);

        // Act
        var jpaEntity = VersionRepositorioArtefactoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(entity.id());
        assertThat(jpaEntity.getNombreDocumento()).isEqualTo("Plantilla");
        assertThat(jpaEntity.getVersionDocumento()).isEqualTo(2);
        assertThat(jpaEntity.getRepositorioArtefacto()).isEqualTo(entity.repositorioArtefacto());
        assertThat(jpaEntity.getNombreRepositorio()).isEqualTo("Repositorio");
        assertThat(jpaEntity.getNombreArchivo()).isEqualTo("plantilla.pdf");
        assertThat(jpaEntity.getTipoContenido()).isEqualTo("application/pdf");
        assertThat(jpaEntity.getTamanoBytes()).isEqualTo(2048L);
    }
}
