package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto.EvaluacionResponseDTO;
import java.math.BigDecimal;
import com.arquisoft.shared.util.UtilUUID;
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
                new EvaluacionReadModel.Estado("PENDIENTE", "Pendiente"),
                new EvaluacionReadModel.Notas(null, null, null),
                null);

        // Act
        var response = EvaluacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response).isEqualTo(new EvaluacionResponseDTO(
                id,
                new EvaluacionResponseDTO.EntregableDTO(entregable, "Robot seguidor", 2),
                new EvaluacionResponseDTO.EstadoDTO("PENDIENTE", "Pendiente"),
                new EvaluacionResponseDTO.NotasDTO(null, null, null),
                null));
    }

    @Test
    void debeMapearNotasYResultado_cuandoEstanPresentes() {
        // Arrange
        var readModel = new EvaluacionReadModel(
                UtilUUID.generarNuevoUUID(),
                new EvaluacionReadModel.Entregable(UtilUUID.generarNuevoUUID(), "Robot seguidor", 2),
                new EvaluacionReadModel.Estado("FINALIZADA", "Finalizada"),
                new EvaluacionReadModel.Notas(new BigDecimal("4.50"), new BigDecimal("3.80"), new BigDecimal("4.10")),
                new EvaluacionReadModel.Resultado("APROBADA", "Aprobada"));

        // Act
        var response = EvaluacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.notas()).isEqualTo(
                new EvaluacionResponseDTO.NotasDTO(new BigDecimal("4.50"), new BigDecimal("3.80"), new BigDecimal("4.10")));
        assertThat(response.resultado()).isEqualTo(new EvaluacionResponseDTO.ResultadoDTO("APROBADA", "Aprobada"));
    }
}
