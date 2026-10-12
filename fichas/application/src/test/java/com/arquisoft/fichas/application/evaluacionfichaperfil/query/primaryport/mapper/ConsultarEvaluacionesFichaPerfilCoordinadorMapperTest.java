package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilCoordinadorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesFichaPerfilCoordinadorMapperTest {

    @Test
    void debeConvertirQueryEnCriteria_cuandoQueryEsValido() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesFichaPerfilCoordinadorQuery.crear(fichaPerfil);

        // Act
        var criteria = ConsultarEvaluacionesFichaPerfilCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
