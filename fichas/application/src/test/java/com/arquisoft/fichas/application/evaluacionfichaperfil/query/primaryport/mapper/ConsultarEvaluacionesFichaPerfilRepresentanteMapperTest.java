package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesFichaPerfilRepresentanteMapperTest {

    @Test
    void debeMapearQueryACriteria_conLosDosIdentificadores() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();
        var query = ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);

        // Act
        EvaluacionFichaPerfilRepresentanteCriteria criteria =
                ConsultarEvaluacionesFichaPerfilRepresentanteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.representanteComite()).isEqualTo(representanteComite);
    }
}
