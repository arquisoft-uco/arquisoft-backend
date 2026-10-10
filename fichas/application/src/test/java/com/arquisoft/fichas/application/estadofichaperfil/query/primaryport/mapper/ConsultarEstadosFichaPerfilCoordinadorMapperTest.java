package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaPerfilCoordinadorMapperTest {

    @Test
    void debeConstruirCriteria_cuandoRecibeQuery() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosFichaPerfilCoordinadorQuery.crear(fichaPerfil);

        // Act
        var criteria = ConsultarEstadosFichaPerfilCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
