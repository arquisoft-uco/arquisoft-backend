package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarAsesoresActualesProyectoGradoEstudianteMapperTest {

    @Test
    void debeConstruirCriteriaConElEstudiante_cuandoQueryValida() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarAsesoresActualesProyectoGradoEstudianteQuery.crear(estudiante);

        // Act
        var criteria = ConsultarAsesoresActualesProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
