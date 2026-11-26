package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionJpaQueryEntity;
import java.math.BigDecimal;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionQueryMapperTest {

    @Test
    void debeMapearIdEntregableYEstado() {
        // Arrange
        var id = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        var entity = new EvaluacionJpaQueryEntity(id, entregable, "Robot seguidor", 3, "FINALIZADA", "Finalizada",
                null, null, null, null, null);

        // Act
        var readModel = EvaluacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel).isEqualTo(new EvaluacionReadModel(
                id,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor", 3),
                new EvaluacionReadModel.Estado("FINALIZADA", "Finalizada"),
                new EvaluacionReadModel.Notas(null, null, null),
                null));
    }

    @Test
    void debeMapearNotasYResultado_cuandoLaEvaluacionEstaConsolidada() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var entity = new EvaluacionJpaQueryEntity(id, entregable, "Robot seguidor", 3, "FINALIZADA", "Finalizada",
                new BigDecimal("4.50"), new BigDecimal("3.80"), new BigDecimal("4.10"),
                "APROBADA", "Aprobada");

        // Act
        var readModel = EvaluacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.notas()).isEqualTo(
                new EvaluacionReadModel.Notas(new BigDecimal("4.50"), new BigDecimal("3.80"), new BigDecimal("4.10")));
        assertThat(readModel.resultado()).isEqualTo(new EvaluacionReadModel.Resultado("APROBADA", "Aprobada"));
    }
}
