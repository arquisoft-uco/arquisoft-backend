package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilRepresentanteMapperTest {

    @Test
    void debeConstruirCriteriaConLosUuidDelQuery() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();
        var query = ConsultarItemsFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);

        // Act
        ItemFichaPerfilRepresentanteCriteria criteria = ConsultarItemsFichaPerfilRepresentanteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.representanteComite()).isEqualTo(representanteComite);
    }
}
