package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilRepresentanteRequestMapperTest {

    @Test
    void debeArmarQueryConPathVariableYSubject() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var representanteId = UUID.randomUUID();

        // Act
        ConsultarItemsFichaPerfilRepresentanteQuery query =
                ConsultarItemsFichaPerfilRepresentanteRequestMapper.toQuery(fichaPerfilId, representanteId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(query.representanteComite()).isEqualTo(representanteId);
    }
}
