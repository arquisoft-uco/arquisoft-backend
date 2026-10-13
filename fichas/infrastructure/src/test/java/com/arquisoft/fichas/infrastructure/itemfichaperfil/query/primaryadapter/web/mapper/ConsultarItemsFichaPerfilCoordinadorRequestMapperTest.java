package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilCoordinadorRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoUUIDValido() {
        // Arrange
        var fichaPerfilId = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarItemsFichaPerfilCoordinadorRequestMapper.toQuery(fichaPerfilId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
    }
}
