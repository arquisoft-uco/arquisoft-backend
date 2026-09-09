package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaPerfilEstudianteMapperTest {

    @Test
    void debeConstruirCriteriaConLosUuidDelQuery() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarEstadosFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Act
        EstadoFichaPerfilEstudianteCriteria criteria = ConsultarEstadosFichaPerfilEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
