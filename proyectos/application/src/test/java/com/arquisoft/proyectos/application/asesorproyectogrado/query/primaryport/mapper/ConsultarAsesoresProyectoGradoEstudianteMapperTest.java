package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarAsesoresProyectoGradoEstudianteMapperTest {

    @Test
    void debeMapearCriteria_cuandoQueryValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarAsesoresProyectoGradoEstudianteQuery.crear(proyectoGrado, estudiante);

        // Act
        var criteria = ConsultarAsesoresProyectoGradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
