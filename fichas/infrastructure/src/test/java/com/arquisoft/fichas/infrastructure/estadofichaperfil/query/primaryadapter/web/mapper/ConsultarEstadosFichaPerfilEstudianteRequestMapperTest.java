package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaPerfilEstudianteRequestMapperTest {

    @Test
    void debeArmarQueryConPathVariableYSubject() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();

        // Act
        ConsultarEstadosFichaPerfilEstudianteQuery query =
                ConsultarEstadosFichaPerfilEstudianteRequestMapper.toQuery(fichaPerfilId, estudianteId);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(query.estudiante()).isEqualTo(estudianteId);
    }
}
