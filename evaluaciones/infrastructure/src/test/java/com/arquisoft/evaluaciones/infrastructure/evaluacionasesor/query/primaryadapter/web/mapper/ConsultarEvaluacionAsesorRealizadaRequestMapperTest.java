package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionAsesorRealizadaRequestMapperTest {

    @Test
    void debeConstruirElQuery_cuandoLosDatosSonValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionAsesorRealizadaRequestMapper.toQuery(evaluacionAsesor, asesor);

        // Assert
        assertThat(query.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(query.asesor()).isEqualTo(asesor);
    }
}
