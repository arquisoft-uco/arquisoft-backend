package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_campoACampo() {
        // Arrange
        var id = UUID.randomUUID();
        var item = UUID.randomUUID();
        var fechaCreacion = Instant.now();
        var readModel = new RevisionItemReadModel(id, item, "EN_PROGRESO", "En Progreso", fechaCreacion);

        // Act
        var response = RevisionItemResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.item()).isEqualTo(item);
        assertThat(response.estadoRevision()).isEqualTo("EN_PROGRESO");
        assertThat(response.estadoRevisionNombre()).isEqualTo("En Progreso");
        assertThat(response.fechaCreacion()).isEqualTo(fechaCreacion);
    }
}
