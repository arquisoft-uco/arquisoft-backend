package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilRepresentanteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaPerfilRepresentanteMapperTest {

    @Test
    void debeMapearQueryACriteria_cuandoSeConvierte() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);

        // Act
        var criteria = ConsultarEstadosFichaPerfilRepresentanteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.representanteComite()).isEqualTo(representanteComite);
    }
}
