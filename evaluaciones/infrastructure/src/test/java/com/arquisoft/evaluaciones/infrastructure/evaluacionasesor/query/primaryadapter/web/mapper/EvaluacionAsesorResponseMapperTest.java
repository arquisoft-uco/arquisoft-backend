package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsesorResponseMapperTest {

    @Test
    void debeMapearIdYAsesor_cuandoSeConvierte() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new EvaluacionAsesorReadModel(id,
                new EvaluacionAsesorReadModel.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co"));

        // Act
        var response = EvaluacionAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.asesor().id()).isEqualTo(asesor);
        assertThat(response.asesor().nombre()).isEqualTo("Ana Gomez");
        assertThat(response.asesor().email()).isEqualTo("ana@uco.edu.co");
    }
}
