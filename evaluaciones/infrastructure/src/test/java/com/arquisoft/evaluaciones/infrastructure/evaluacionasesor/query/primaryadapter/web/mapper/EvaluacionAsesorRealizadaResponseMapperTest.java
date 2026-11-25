package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsesorRealizadaResponseDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsesorRealizadaResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoSeConvierte() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new EvaluacionAsesorRealizadaReadModel(id,
                new EvaluacionAsesorRealizadaReadModel.Evaluacion(evaluacion,
                        new EvaluacionAsesorRealizadaReadModel.Entregable(entregable, "Robot seguidor", 2),
                        new EvaluacionAsesorRealizadaReadModel.Estado("EN_PROGRESO", "En progreso")),
                new EvaluacionAsesorRealizadaReadModel.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co"),
                new EvaluacionAsesorRealizadaReadModel.Notas(null, null, null));

        // Act
        var response = EvaluacionAsesorRealizadaResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response).isEqualTo(new EvaluacionAsesorRealizadaResponseDTO(id,
                new EvaluacionAsesorRealizadaResponseDTO.Evaluacion(evaluacion,
                        new EvaluacionAsesorRealizadaResponseDTO.Entregable(entregable, "Robot seguidor", 2),
                        new EvaluacionAsesorRealizadaResponseDTO.Estado("EN_PROGRESO", "En progreso")),
                new EvaluacionAsesorRealizadaResponseDTO.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co"),
                new EvaluacionAsesorRealizadaResponseDTO.Notas(null, null, null)));
    }

    @Test
    void debeMapearNotas_cuandoEstanCalculadas() {
        // Arrange
        var readModel = new EvaluacionAsesorRealizadaReadModel(UtilUUID.generarNuevoUUID(),
                new EvaluacionAsesorRealizadaReadModel.Evaluacion(UtilUUID.generarNuevoUUID(),
                        new EvaluacionAsesorRealizadaReadModel.Entregable(UtilUUID.generarNuevoUUID(), "Robot seguidor", 2),
                        new EvaluacionAsesorRealizadaReadModel.Estado("FINALIZADA", "Finalizada")),
                new EvaluacionAsesorRealizadaReadModel.Asesor(UtilUUID.generarNuevoUUID(), "Ana Gomez", "ana@uco.edu.co"),
                new EvaluacionAsesorRealizadaReadModel.Notas(new BigDecimal("4.5"), new BigDecimal("3.8"), new BigDecimal("4.1")));

        // Act
        var response = EvaluacionAsesorRealizadaResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.notas()).isEqualTo(
                new EvaluacionAsesorRealizadaResponseDTO.Notas(new BigDecimal("4.5"), new BigDecimal("3.8"), new BigDecimal("4.1")));
    }
}
