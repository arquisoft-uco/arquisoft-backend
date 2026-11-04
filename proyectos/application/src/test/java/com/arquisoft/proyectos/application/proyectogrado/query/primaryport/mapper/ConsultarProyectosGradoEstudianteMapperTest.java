package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarProyectosGradoEstudianteMapperTest {

    @Test
    void debeMapearEstudianteAlCriteria_cuandoQueryValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarProyectosGradoEstudianteQuery.crear(estudiante);

        // Act
        var criteria = ConsultarProyectosGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
