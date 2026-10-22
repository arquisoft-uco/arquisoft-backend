package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoRealizadasQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCualitativasJuradoRealizadasMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCualitativasJuradoRealizadasQuery.crear(evaluacionJurado, jurado);

        // Act
        var criteria = ConsultarEvaluacionesCualitativasJuradoRealizadasMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(criteria.jurado()).isEqualTo(jurado);
    }
}
