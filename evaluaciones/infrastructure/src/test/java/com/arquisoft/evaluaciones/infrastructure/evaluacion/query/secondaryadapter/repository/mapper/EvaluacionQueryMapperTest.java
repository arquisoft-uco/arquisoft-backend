package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionQueryMapperTest {

    @Test
    void debeMapearIdEntregableYEstado() {
        // Arrange
        var id = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        var entity = new EvaluacionJpaQueryEntity(id, entregable, "Robot seguidor", 3, "FINALIZADA", "Finalizada");

        // Act
        var readModel = EvaluacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel).isEqualTo(new EvaluacionReadModel(
                id,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor", 3),
                new EvaluacionReadModel.Estado("FINALIZADA", "Finalizada")));
    }
}
