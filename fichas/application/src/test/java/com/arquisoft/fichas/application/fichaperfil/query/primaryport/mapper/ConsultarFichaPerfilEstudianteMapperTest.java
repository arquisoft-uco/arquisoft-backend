package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarFichaPerfilEstudianteMapperTest {

    @Test
    void debeMapearQueryACriteria_cuandoDatosValidos() {
        // Arrange
        var query = ConsultarFichaPerfilEstudianteQuery.crear(UUID.randomUUID(), UUID.randomUUID());

        // Act
        var criteria = ConsultarFichaPerfilEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(query.fichaPerfil());
        assertThat(criteria.estudiante()).isEqualTo(query.estudiante());
    }
}
