package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstudiantesFichaPerfilRequestMapperTest {

    @Test
    void debeCrearQueryDesdeFichaPerfilId() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();

        // Act
        ConsultarEstudiantesFichaPerfilQuery query =
                ConsultarEstudiantesFichaPerfilRequestMapper.toQuery(fichaPerfilId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
    }
}
