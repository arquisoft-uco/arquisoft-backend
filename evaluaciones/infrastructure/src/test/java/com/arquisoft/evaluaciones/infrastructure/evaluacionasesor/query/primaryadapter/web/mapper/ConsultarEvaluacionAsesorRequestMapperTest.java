package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionAsesorRequestMapperTest {

    @Test
    void debeConstruirElQuery_cuandoElIdEsValido() {
        // Arrange
        var evaluacionId = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionAsesorRequestMapper.toQuery(evaluacionId);

        // Assert
        assertThat(query.evaluacion()).isEqualTo(evaluacionId);
    }
}
