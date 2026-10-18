package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarEstadosProyectoGradoEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosProyectoGradoEstudianteMapperTest {

    @Test
    void debeConvertirQueryEnCriteria_cuandoQueryValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosProyectoGradoEstudianteQuery.crear(proyectoGrado, estudiante);

        // Act
        var criteria = ConsultarEstadosProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
