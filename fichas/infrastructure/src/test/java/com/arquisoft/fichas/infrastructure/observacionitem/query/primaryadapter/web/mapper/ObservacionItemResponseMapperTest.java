package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_campoACampo() {
        // Arrange
        var id = UUID.randomUUID();
        var revisionItem = UUID.randomUUID();
        var readModel = new ObservacionItemReadModel(
                id, revisionItem, "Falta precisar el alcance", "EN_PROGRESO", "En Progreso");

        // Act
        var response = ObservacionItemResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.revisionItem()).isEqualTo(revisionItem);
        assertThat(response.observacion()).isEqualTo("Falta precisar el alcance");
        assertThat(response.estadoObservacionRevision()).isEqualTo("EN_PROGRESO");
        assertThat(response.estadoObservacionRevisionNombre()).isEqualTo("En Progreso");
    }
}
