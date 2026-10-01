package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilAsesorQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilAsesorMapperTest {

    @Test
    void debeConstruirCriteriaConLosUuidDelQuery() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();
        var query = ConsultarItemsFichaPerfilAsesorQuery.crear(fichaPerfil, asesorFicha);

        // Act
        ItemFichaPerfilAsesorCriteria criteria = ConsultarItemsFichaPerfilAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.asesorFicha()).isEqualTo(asesorFicha);
    }
}
