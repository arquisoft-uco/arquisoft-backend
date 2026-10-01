package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilEstudianteMapperTest {

    @Test
    void debeConstruirCriteriaConLosUuidDelQuery() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarItemsFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Act
        ItemFichaPerfilEstudianteCriteria criteria = ConsultarItemsFichaPerfilEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
