package com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.domain.observacionitem.ObservacionItemDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemMapperTest {

    @Test
    void debeMapearDomainAEntity_cuandoSeConvierte() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var observacionItem = ObservacionItemDomain.crear(revisionItem, "Observación válida");

        // Act
        var entity = ObservacionItemMapper.toEntity(observacionItem);

        // Assert — el estado viaja como el getId() del enum, nunca el objeto
        assertThat(entity.id()).isEqualTo(observacionItem.getId());
        assertThat(entity.revisionItem()).isEqualTo(revisionItem);
        assertThat(entity.observacion()).isEqualTo("Observación válida");
        assertThat(entity.estadoObservacionRevision()).isEqualTo("PENDIENTE");
    }
}
