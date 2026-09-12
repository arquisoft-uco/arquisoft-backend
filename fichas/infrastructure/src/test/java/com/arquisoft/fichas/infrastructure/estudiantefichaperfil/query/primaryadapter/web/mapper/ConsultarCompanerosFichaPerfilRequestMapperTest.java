package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarCompanerosFichaPerfilQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarCompanerosFichaPerfilRequestMapperTest {

    @Test
    void debeCrearQueryDesdeFichaPerfilIdYEstudianteId() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();

        // Act
        ConsultarCompanerosFichaPerfilQuery query =
                ConsultarCompanerosFichaPerfilRequestMapper.toQuery(fichaPerfilId, estudianteId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(query.estudiante()).isEqualTo(estudianteId);
    }
}
