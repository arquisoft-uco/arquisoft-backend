package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarCompanerosProyectoGradoMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarCompanerosProyectoGradoQuery.crear(proyectoGrado, estudiante);

        // Act
        var criteria = ConsultarCompanerosProyectoGradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
