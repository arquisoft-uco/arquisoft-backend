package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemJpaMapperTest {

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeConvierte() {
        // Arrange
        var id = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var fechaCreacion = Instant.now();
        var estadoRevision = EstadoRevisionJpaEntity.builder()
                .id("NUEVA")
                .nombre("Nueva")
                .descripcion("La revision ha sido creada recientemente")
                .build();
        var jpaEntity = RevisionItemJpaEntity.builder()
                .id(id)
                .itemId(itemId)
                .estadoRevision(estadoRevision)
                .fechaCreacion(fechaCreacion)
                .build();

        // Act
        var entity = RevisionItemJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.item()).isEqualTo(itemId);
        assertThat(entity.estadoRevision()).isEqualTo("NUEVA");
        assertThat(entity.fechaCreacion()).isEqualTo(fechaCreacion);
    }

    @Test
    void debeMapearEntityAJpaEntity_construyendoReferenciaSoloConId() {
        // Arrange
        var id = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var fechaCreacion = Instant.now();
        var entity = new RevisionItemEntity(id, itemId, "NUEVA", fechaCreacion);

        // Act
        var jpaEntity = RevisionItemJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(id);
        assertThat(jpaEntity.getItemId()).isEqualTo(itemId);
        assertThat(jpaEntity.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(jpaEntity.getEstadoRevision().getId()).isEqualTo("NUEVA");
        assertThat(jpaEntity.getEstadoRevision().getNombre()).isNull();
        assertThat(jpaEntity.getEstadoRevision().getDescripcion()).isNull();
    }
}
