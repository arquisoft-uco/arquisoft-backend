package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarItemsFichaPerfilEstudianteRequestMapperTest {

    @Test
    void debeArmarQueryConPathVariableYSubject() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();

        // Act
        ConsultarItemsFichaPerfilEstudianteQuery query =
                ConsultarItemsFichaPerfilEstudianteRequestMapper.toQuery(fichaPerfilId, estudianteId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(query.estudiante()).isEqualTo(estudianteId);
    }
}
