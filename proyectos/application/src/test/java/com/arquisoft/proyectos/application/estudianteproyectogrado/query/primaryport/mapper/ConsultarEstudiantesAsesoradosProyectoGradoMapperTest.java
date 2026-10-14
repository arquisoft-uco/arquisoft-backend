package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstudiantesAsesoradosProyectoGradoMapperTest {

    @Test
    void debeMapearCriteria_cuandoQueryValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstudiantesAsesoradosProyectoGradoQuery.crear(proyectoGrado, asesor);

        // Act
        var criteria = ConsultarEstudiantesAsesoradosProyectoGradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.asesor()).isEqualTo(asesor);
    }
}
