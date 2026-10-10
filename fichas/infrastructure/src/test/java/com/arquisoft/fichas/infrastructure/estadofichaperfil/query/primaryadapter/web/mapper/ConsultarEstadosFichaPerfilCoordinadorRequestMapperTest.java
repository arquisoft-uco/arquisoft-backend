package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaPerfilCoordinadorRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoUuidValido() {
        // Arrange
        var fichaPerfilId = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstadosFichaPerfilCoordinadorRequestMapper.toQuery(fichaPerfilId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
    }
}
