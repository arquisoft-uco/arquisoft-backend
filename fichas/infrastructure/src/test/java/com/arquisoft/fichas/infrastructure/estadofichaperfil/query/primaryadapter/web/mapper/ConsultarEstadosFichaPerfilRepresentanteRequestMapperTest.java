package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaPerfilRepresentanteRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoIdsValidos() {
        // Arrange
        var fichaPerfilId = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstadosFichaPerfilRepresentanteRequestMapper.toQuery(fichaPerfilId, representanteComite);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(query.representanteComite()).isEqualTo(representanteComite);
    }
}
