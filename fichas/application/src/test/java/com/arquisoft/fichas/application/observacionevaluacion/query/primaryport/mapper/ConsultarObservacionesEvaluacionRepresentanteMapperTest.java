package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionRepresentanteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesEvaluacionRepresentanteMapperTest {

    @Test
    void debeConvertirQueryEnCriteria_cuandoQueryEsValido() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionRepresentanteQuery.crear(evaluacionFichaPerfil, representanteComite);

        // Act
        var criteria = ConsultarObservacionesEvaluacionRepresentanteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(criteria.representanteComite()).isEqualTo(representanteComite);
    }
}
