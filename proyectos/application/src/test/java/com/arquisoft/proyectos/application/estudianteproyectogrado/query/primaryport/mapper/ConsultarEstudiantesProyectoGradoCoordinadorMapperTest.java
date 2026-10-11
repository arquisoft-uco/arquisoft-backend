package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesProyectoGradoCoordinadorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstudiantesProyectoGradoCoordinadorMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstudiantesProyectoGradoCoordinadorQuery.crear(proyectoGrado, coordinador);

        // Act
        var criteria = ConsultarEstudiantesProyectoGradoCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.coordinador()).isEqualTo(coordinador);
    }
}
