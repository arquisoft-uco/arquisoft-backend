package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilAsesorQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilAsesorRequestMapperTest {

    @Test
    void debeArmarQueryConPathVariableYSubject() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();

        // Act
        ConsultarItemsFichaPerfilAsesorQuery query =
                ConsultarItemsFichaPerfilAsesorRequestMapper.toQuery(fichaPerfilId, asesorFicha);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
    }
}
