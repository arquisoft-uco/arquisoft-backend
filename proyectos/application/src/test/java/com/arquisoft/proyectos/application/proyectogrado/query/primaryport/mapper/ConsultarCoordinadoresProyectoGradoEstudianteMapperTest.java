package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarCoordinadoresProyectoGradoEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarCoordinadoresProyectoGradoEstudianteMapperTest {

    @Test
    void debeConstruirCriteriaConElEstudiante_cuandoQueryValida() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarCoordinadoresProyectoGradoEstudianteQuery.crear(estudiante);

        // Act
        var criteria = ConsultarCoordinadoresProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
