package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionEstudianteJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionEstudianteQueryMapperTest {

    @Test
    void debeMapearIdEntregableYEstado_ignorandoEstudianteYProyectoDeGrado() {
        // Arrange
        var id = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        var entity = new EvaluacionEstudianteJpaQueryEntity(id, UUID.randomUUID(), UUID.randomUUID(), entregable,
                "Robot seguidor", 3, "FINALIZADA", "Finalizada");

        // Act
        var readModel = EvaluacionEstudianteQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel).isEqualTo(new EvaluacionReadModel(
                id,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor", 3),
                new EvaluacionReadModel.Estado("FINALIZADA", "Finalizada")));
    }
}
