package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemJpaMapperTest {

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeConvierte() {
        // Arrange
        var id = UUID.randomUUID();
        var revisionItemId = UUID.randomUUID();
        var estado = EstadoObservacionRevisionJpaEntity.builder()
                .id("PENDIENTE")
                .nombre("Pendiente")
                .descripcion("La observación ha sido registrada")
                .build();
        var jpaEntity = ObservacionItemJpaEntity.builder()
                .id(id)
                .revisionItemId(revisionItemId)
                .observacion("Observación válida")
                .estadoObservacionRevision(estado)
                .build();

        // Act
        var entity = ObservacionItemJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.revisionItem()).isEqualTo(revisionItemId);
        assertThat(entity.observacion()).isEqualTo("Observación válida");
        assertThat(entity.estadoObservacionRevision()).isEqualTo("PENDIENTE");
    }

    @Test
    void debeMapearEntityAJpaEntity_construyendoReferenciaSoloConId() {
        // Arrange
        var id = UUID.randomUUID();
        var revisionItemId = UUID.randomUUID();
        var entity = new ObservacionItemEntity(id, revisionItemId, "Observación válida", "PENDIENTE");

        // Act
        var jpaEntity = ObservacionItemJpaMapper.toJpaEntity(entity);

        // Assert — el revision_item_id es una FK plana, sin @ManyToOne
        assertThat(jpaEntity.getId()).isEqualTo(id);
        assertThat(jpaEntity.getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(jpaEntity.getObservacion()).isEqualTo("Observación válida");
        assertThat(jpaEntity.getEstadoObservacionRevision().getId()).isEqualTo("PENDIENTE");
        assertThat(jpaEntity.getEstadoObservacionRevision().getNombre()).isNull();
        assertThat(jpaEntity.getEstadoObservacionRevision().getDescripcion()).isNull();
    }
}
