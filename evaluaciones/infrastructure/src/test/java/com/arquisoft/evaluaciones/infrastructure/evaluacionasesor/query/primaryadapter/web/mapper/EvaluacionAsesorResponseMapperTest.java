package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsesorResponseDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsesorResponseMapperTest {

    @Test
    void debeMapearIdYAsesor_cuandoSeConvierte() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new EvaluacionAsesorReadModel(id,
                new EvaluacionAsesorReadModel.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co"),
                new EvaluacionAsesorReadModel.Notas(null, null, null));

        // Act
        var response = EvaluacionAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.asesor().id()).isEqualTo(asesor);
        assertThat(response.asesor().nombre()).isEqualTo("Ana Gomez");
        assertThat(response.asesor().email()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeMapearNotas_cuandoEstanCalculadas() {
        // Arrange
        var readModel = new EvaluacionAsesorReadModel(UtilUUID.generarNuevoUUID(),
                new EvaluacionAsesorReadModel.Asesor(UtilUUID.generarNuevoUUID(), "Ana Gomez", "ana@uco.edu.co"),
                new EvaluacionAsesorReadModel.Notas(new BigDecimal("4.5"), new BigDecimal("3.8"), new BigDecimal("4.1")));

        // Act
        var response = EvaluacionAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.notas()).isEqualTo(
                new EvaluacionAsesorResponseDTO.Notas(new BigDecimal("4.5"), new BigDecimal("3.8"), new BigDecimal("4.1")));
    }

    @Test
    void debePropagarNulos_cuandoNoSeCalcularon() {
        // Arrange
        var readModel = new EvaluacionAsesorReadModel(UtilUUID.generarNuevoUUID(),
                new EvaluacionAsesorReadModel.Asesor(UtilUUID.generarNuevoUUID(), "Ana Gomez", "ana@uco.edu.co"),
                new EvaluacionAsesorReadModel.Notas(null, null, null));

        // Act
        var response = EvaluacionAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.notas()).isEqualTo(new EvaluacionAsesorResponseDTO.Notas(null, null, null));
    }
}
