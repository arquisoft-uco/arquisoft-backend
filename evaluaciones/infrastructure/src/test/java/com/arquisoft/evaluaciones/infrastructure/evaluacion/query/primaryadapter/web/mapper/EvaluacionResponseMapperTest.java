package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto.EvaluacionResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionResponseMapperTest {

    @Test
    void debeMapearIdEntregableYEstado() {
        // Arrange
        var id = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        var readModel = new EvaluacionReadModel(
                id,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor", 2),
                new EvaluacionReadModel.Estado("PENDIENTE", "Pendiente"));

        // Act
        var response = EvaluacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response).isEqualTo(new EvaluacionResponseDTO(
                id,
                new EvaluacionResponseDTO.EntregableDTO(entregable, "Robot seguidor", 2),
                new EvaluacionResponseDTO.EstadoDTO("PENDIENTE", "Pendiente")));
    }
}
