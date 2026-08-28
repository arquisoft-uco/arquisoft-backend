package com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemMapperTest {

    @Test
    void debeMapearDominioAEntidad_cuandoSeConvierte() {
        // Arrange
        var revisionItem = RevisionItemDomain.crear(UUID.randomUUID(), "NUEVA");

        // Act
        var entity = RevisionItemMapper.toEntity(revisionItem);

        // Assert
        assertThat(entity.id()).isEqualTo(revisionItem.getId());
        assertThat(entity.item()).isEqualTo(revisionItem.getItem());
        assertThat(entity.estadoRevision()).isEqualTo(revisionItem.getEstadoRevision().getId());
        assertThat(entity.fechaCreacion()).isEqualTo(revisionItem.getFechaCreacion());
    }
}
