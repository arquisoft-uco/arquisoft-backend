package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarFichasPerfilEstudianteMapperTest {

    @Test
    void debeMapearQueryACriteria_cuandoDatosValidos() {
        // Arrange
        var query = ConsultarFichasPerfilEstudianteQuery.crear(UUID.randomUUID());

        // Act
        var criteria = ConsultarFichasPerfilEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(query.estudiante());
    }
}
