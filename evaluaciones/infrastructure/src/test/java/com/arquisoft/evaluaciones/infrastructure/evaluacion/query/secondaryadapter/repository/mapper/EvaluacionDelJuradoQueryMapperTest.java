package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionDelJuradoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionDelJuradoQueryMapperTest {

    @Test
    void debeMapearNotasYResultado_cuandoLaEvaluacionEstaConsolidada() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var entity = new EvaluacionDelJuradoJpaQueryEntity(id, UtilUUID.generarNuevoUUID(), entregable,
                "Robot seguidor", 3, "FINALIZADA", "Finalizada",
                new BigDecimal("4.50"), new BigDecimal("3.80"), new BigDecimal("4.10"), "APROBADA", "Aprobada");

        // Act
        var readModel = EvaluacionDelJuradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel).isEqualTo(new EvaluacionReadModel(
                id,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor", 3),
                new EvaluacionReadModel.Estado("FINALIZADA", "Finalizada"),
                new EvaluacionReadModel.Notas(new BigDecimal("4.50"), new BigDecimal("3.80"), new BigDecimal("4.10")),
                new EvaluacionReadModel.Resultado("APROBADA", "Aprobada")));
    }

    @Test
    void debeDevolverNotasNulasYResultadoNulo_cuandoNoSeConsolido() {
        // Arrange
        var entity = new EvaluacionDelJuradoJpaQueryEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Robot seguidor", 3, "PENDIENTE", "Pendiente",
                null, null, null, null, null);

        // Act
        var readModel = EvaluacionDelJuradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.notas()).isEqualTo(new EvaluacionReadModel.Notas(null, null, null));
        assertThat(readModel.resultado()).isNull();
    }
}
