package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsesorRealizadaResponseDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

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
                new EvaluacionAsesorRealizadaReadModel.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co"));

        // Act
        var response = EvaluacionAsesorRealizadaResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response).isEqualTo(new EvaluacionAsesorRealizadaResponseDTO(id,
                new EvaluacionAsesorRealizadaResponseDTO.Evaluacion(evaluacion,
                        new EvaluacionAsesorRealizadaResponseDTO.Entregable(entregable, "Robot seguidor", 2),
                        new EvaluacionAsesorRealizadaResponseDTO.Estado("EN_PROGRESO", "En progreso")),
                new EvaluacionAsesorRealizadaResponseDTO.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co")));
    }
}
