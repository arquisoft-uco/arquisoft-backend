package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCuantitativasJuradoRealizadasRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesCuantitativasJuradoRealizadasRequestMapper.toQuery(evaluacionJurado, jurado);

        // Assert
        assertThat(query.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(query.jurado()).isEqualTo(jurado);
    }
}
