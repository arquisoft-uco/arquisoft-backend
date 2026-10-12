package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilCoordinadorMapperTest {

    @Test
    void debeMapearFichaPerfil_cuandoQueryValida() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarItemsFichaPerfilCoordinadorQuery.crear(fichaPerfil);

        // Act
        var criteria = ConsultarItemsFichaPerfilCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
