package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCuantitativasJuradoRealizadasMapperTest {

    @Test
    void debeMapearCriteria_cuandoQueryValida() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery.crear(evaluacionJurado, jurado);

        // Act
        var criteria = ConsultarEvaluacionesCuantitativasJuradoRealizadasMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(criteria.jurado()).isEqualTo(jurado);
    }
}
